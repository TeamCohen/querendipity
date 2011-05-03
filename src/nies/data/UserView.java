package nies.data;

import java.util.Collection;

import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.SerialSerialBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredMap;

public class UserView implements DataView {
	private StoredMap<String, User> userMap;
	private StoredMap<String, User> userByUsernameMap;
	public StoredMap<String,User> getUserByUsernameMap() {
		return userByUsernameMap;
	}
	public StoredMap<String,User> getUserMap() {
		return userMap;
	}
	public StoredEntrySet<String,User> getUserByUsernameEntrySet() {
		return (StoredEntrySet<String, User>) userByUsernameMap.entrySet();
	}
	public StoredEntrySet<String,User> getUserEntrySet() {
		return (StoredEntrySet<String, User>) userMap.entrySet();
	}
	public UserView (UserDatabase db) {
		ClassCatalog catalog = db.getJavaCatalog();
		SerialBinding<String> userKeyBinding     = new SerialBinding<String>(catalog, String.class);
		SerialBinding<String> usernameKeyBinding = new SerialBinding<String>(catalog, String.class);
		EntityBinding<User>   userValueBinding   = new UserBinding(catalog, String.class, UserData.class);
		this.userMap           = new StoredMap<String, User>(db.getUserDB(),          userKeyBinding,     userValueBinding, true);
		this.userByUsernameMap = new StoredMap<String, User>(db.getUserByUsernameDB(),usernameKeyBinding, userValueBinding, true);
	}
	
	private static class UserBinding extends SerialSerialBinding<String, UserData, User> {
		private UserBinding(ClassCatalog catalog, Class<String> keyclass, Class<UserData> dataclass) {
			super(catalog, keyclass, dataclass);
		}
		public User entryToObject(String uid, UserData data) {
			return new User(uid, data.getUsername(), data.getPassHash(), data.getEmail());
		}
		public String objectToKey(User user) {
			return new String(user.getUid());
		}
		public UserData objectToData(User user) {
			return new UserData(user.getUsername(), user.getPassHash(), user.getEmail());
		}
	}

	public Collection<User> getUserSet() {
		return this.userMap.values();
	}

	public StoredMap<String,?> getPrimaryKeyMap() { return userMap; }
}
