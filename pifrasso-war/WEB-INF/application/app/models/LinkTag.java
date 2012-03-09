package models;

import java.util.*;
import siena.*;

public class LinkTag extends Model {

    @Id
    public Long id;

	@NotNull
	@Column("link_id")
    @Index("link_index")
	public Link link;

	@NotNull
	@Column("tag_id")
    @Index("tag_index")
	public Tag tag;

	public LinkTag(Link link, Tag tag) {
		super();
		this.link = link;
		this.tag = tag;
	}

	public static Query<LinkTag> all() {
		return Model.all(LinkTag.class);
	}

	public static List<Tag> findByLink(Link link) {
		List<LinkTag> linkTags =  all().filter("link", link).fetch();
		List<Tag> tags = new ArrayList<Tag>();
		for(LinkTag linkTag : linkTags) {
			tags.add(Tag.findById(linkTag.tag.id));
		}

		return tags;
	}
	public static List<Link> findByTag(Tag tag) {
		List<LinkTag> linkTags =  all().filter("tag", tag).fetch();
		List<Link> links = new ArrayList<Link>();
		for(LinkTag linkTag : linkTags) {
			links.add(Link.findById(linkTag.link.id));
		}

		return links;
	}

	public String toString() {
		return link.toString() + " : " + tag.toString();
	}

	public static void deleteByLink(Link link) {

		List<LinkTag> linkTags = all().filter("link", link).fetch();
		if(null != linkTags) {
			for(LinkTag linktag : linkTags) {
					linktag.delete();
			}
		}
	}
}

