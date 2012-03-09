package controllers;

import models.*;
import play.*;
import play.mvc.*;
import java.util.*;

import play.data.validation.*;
import java.net.*;
import net.htmlparser.jericho.*;
import java.io.*;

import play.Logger;
import models.Tag;

public class Profile extends Application {

    @Before
    static void checkConnected() {
        if(Auth.getUser() == null) {
            Application.login();
        } else {
            renderArgs.put("user", Auth.getEmail());
        }
    }

    public static void index(String tag) {
		User user = getUser();

		List<Link> links = null;
		if(null == tag || tag.equalsIgnoreCase("")) {
			links = Link.findByUser(user);
		} else {
			links = LinkTag.findByTag(Tag.findByName(tag, user));
		}

		List<Tag> tags = Tag.findByUser(user);

        render(user, links, tags);
    }
	public static void addlink(@Required @play.data.validation.URL String url) {

		if(validation.hasErrors()) {
			flash.error("Please enter a valid URL");
			index(null);
		}
		String tagcsv = params.get("tags");
		User user = getUser();
		Link link = new Link();
		link.url = url;
		link.user = user;
		link.created = new Date();
		try {
			linkGetMetaData(url, link);
		}catch(Exception e) {
			Logger.error(e, "error");
		}
		link.insert();
		if(null != tagcsv) {
			Link.addTagsFromCSV(link, tagcsv, user);
		}
		flash.success("Link %s saved successfully!!", url);
		index(null);

	}
    public static void edit(Long userId) {
		notFoundIfNull(userId);
		User user = User.findById(userId);
		notFoundIfNull(user);
		checkSelfUser(user);


		String name = params.get("user.name");
		if(null == name || "".equals(name)) {
			render(user);
			return;
		}

		user.name = name;
		user.modified = new Date();
		user.update();

		index(null);
	}

	public static void editlink(Long id) {
   		notFoundIfNull(id);
		Link link = Link.findById(id);
        notFoundIfNull(link);
		checkOwner(link);
		List<Tag> tags = link.findTagsByLink();

		String title = params.get("link.title");
		if(title != null) {
			link.title = title;
			link.description = params.get("link.description");

			String tagcsv = params.get("link.tags");
			if(null != tagcsv) {
				LinkTag.deleteByLink(link);
				Link.addTagsFromCSV(link, tagcsv, getUser());
			}
			link.modified = new Date();
			link.update();
			index(null);
			return;
		}

		render(link, tags);

	}

	public static void removeLink(Long id) {
        Link link = Link.findById(id);
        notFoundIfNull(link);
        checkOwner(link);
        LinkTag.deleteByLink(link);
        link.delete();
        flash.success("The link %s has been deleted successfully", link.url);
        index(null);

	}

	private static void linkGetMetaData(String sourceUrlString, Link link) throws Exception {

		MicrosoftTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		Source source=new Source(new java.net.URL(sourceUrlString));

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

		link.title = getTitle(source);

		link.description = getMetaValue(source,"description");
  }

	private static String getTitle(Source source) {
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if (titleElement==null) return null;
		// TITLE element never contains other tags so just decode it collapsing whitespace:
		return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
	}

	private static String getMetaValue(Source source, String key) {
		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,"name",key,false);
			if (startTag==null) return null;
			if (startTag.getName()==HTMLElementName.META)
				return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
			pos=startTag.getEnd();
		}
		return null;
	}

    static User getUser() {

		return User.findByEmail(Auth.getEmail());
    }

    static void checkOwner(Link link) {
        if(!getUser().equals(link.user)) {
            forbidden();
        }
    }
    static void checkSelfUser(User user) {
		if(!getUser().equals(user)) {
			forbidden();
		}
	}
}

