package models;

import siena.*;
import java.util.*;

public class Tag extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public String name;

	@Index("user_index")
	public User user;


    public static Query<Tag> all() {
        return Model.all(Tag.class);
    }

	public static Tag findOrCreateByName(String name, User user) {
        Tag tag = all().filter("user", user).filter("name", name).get();
        if(null == tag) {
			tag = new Tag();
			tag.name = name;
			tag.user = user;
			tag.insert();
		}
		return tag;
	}

	public static Tag findById(Long id) {
		return  all().filter("id", id).get();
	}

    public static Tag findByName(String name, User user) {
        return all().filter("user", user).filter("name", name).get();
    }

    public static List<Tag> findByUser(User user) {
        return all().filter("user", user).fetch();
    }

    public String toString() {
        return name;
    }
}

