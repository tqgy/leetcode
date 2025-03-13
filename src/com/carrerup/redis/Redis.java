// package com.carrerup.redis;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.Iterator;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.UUID;
// import java.util.logging.Logger;

// import redis.clients.jedis.Jedis;
// import redis.clients.jedis.JedisPool;
// import redis.clients.jedis.JedisPoolConfig;
// import redis.clients.jedis.exceptions.JedisConnectionException;

// import com.internalshare.share.module.Comment;
// import com.internalshare.share.module.Feed;
// import com.internalshare.share.module.Range;
// import com.internalshare.share.module.Share;
// import com.internalshare.share.service.UserSecurity;
// import com.internalshare.share.util.KeyUtils;
// import com.internalshare.share.util.StringUtils;

// /**
//  * Cache using Redis
//  * 
//  * @author guyu
//  * 
//  */
// public class Redis {
// 	private static Logger logger = Logger.getLogger(Redis.class.toString());

// 	private static final String SERVER = "42.96.186.145";

// 	static final String name = "name";
// 	static final String uid = "uid";
// 	static final String content = "content";
// 	static final String replyPid = "replyPid";
// 	static final String replyUid = "replyUid";
// 	static final String time = "time";
// 	static final String icon = "icon";
// 	static final String title = "title";
// 	static final String like = "like";
// 	static final String img = "img";
// 	static final String link = "link";
// 	static final String sid = "sid";
// 	static final String cid = "cid";
// 	static final String rootShare = "rootShare";

// 	private static Redis instance = null;

// 	private static JedisPool pool;

// 	public static synchronized void init() {
// 		if (instance == null) {
// 			instance = new Redis();
// 		}
// 	}

// 	public static Redis getInstance() {
// 		if (instance == null) {
// 			init();
// 		}
// 		return instance;
// 	}

// 	private Redis() {
// 		pool = new JedisPool(new JedisPoolConfig(), SERVER);
// 	}

// 	private static void returnJedis(Jedis jedis, boolean broken) {
// 		if (jedis == null) {
// 			return;
// 		}
// 		if (broken) {
// 			pool.returnBrokenResource(jedis);
// 		} else {
// 			pool.returnResource(jedis);
// 		}
// 	}

// 	// public static Jedis redis;

// 	// static {
// 	// init();
// 	// }

// 	// private static final Pattern MENTION_REGEX = Pattern.compile("@[\\w]+");

// 	// global users
// 	public static List<String> getUsers() {
// 		Jedis redis = null;
// 		boolean broken = false;
// 		try {
// 			redis = pool.getResource();
// 			return redis.lrange("global:users", 0, -1);
// 		} catch (JedisConnectionException e) {
// 			broken = true;
// 			logger.severe("Error when handling Redis! " + e.getMessage());
// 		} finally {
// 			returnJedis(redis, broken);
// 		}
// 		return null;

// 	}

// 	// global timeline
// 	private static ArrayList<String> timeline;

// 	/**
// 	 * Get user ids for certain key
// 	 * 
// 	 * @param key
// 	 * @param range
// 	 * @return
// 	 */
// 	private static Set<String> getUids(String key, Range range) {
// 		logger.info("Get uids key : " + key + " Range : " + range);
// 		Jedis redis = null;
// 		boolean broken = false;
// 		try {
// 			redis = pool.getResource();
// 			// Set<String> ids = redis.zrange(key, range.begin, range.end);
// 			Set<String> ids = redis.smembers(key);
// 			return ids;
// 		} catch (JedisConnectionException e) {
// 			broken = true;
// 			logger.severe("Error when handling Redis! " + e.getMessage());
// 		} finally {
// 			returnJedis(redis, broken);
// 		}
// 		return null;

// 	}

// 	@Deprecated
// 	public static boolean auth(String user, String pass) {
// 		// find uid
// 		String uid = findUid(user);
// 		logger.info("Auth:findUid : " + uid);
// 		if (StringUtils.hasText(uid)) {
// 			String p = redis.hget(KeyUtils.uid(uid), pass);
// 			return pass.equals(p) ? true : false;
// 		}
// 		logger.info("No uid");
// 		return false;
// 	}

// 	@Deprecated
// 	public static String findNameForAuth(String value) {
// 		String uid = redis.get(KeyUtils.authKey(value));
// 		return findName(uid);
// 	}

// 	@Deprecated
// 	public static String addAuth(String name) {
// 		String uid = findUid(name);
// 		logger.fine("addAuth:findUid : " + uid);
// 		// add random auth key relation
// 		String auth = UUID.randomUUID().toString();
// 		logger.fine("Auth->auth : " + KeyUtils.authKey(auth));
// 		logger.fine("Auth->uid : " + KeyUtils.auth(uid));
// 		redis.set(KeyUtils.auth(uid), auth);
// 		redis.set(KeyUtils.authKey(auth), uid);

// 		return auth;
// 	}

// 	@Deprecated
// 	public static void deleteAuth(String user) {
// 		String uid = findUid(user);
// 		String authKey = KeyUtils.auth(uid);

// 		redis.del(KeyUtils.auth(uid));
// 		redis.del(KeyUtils.authKey(authKey));
// 	}

// 	/**
// 	 * Get my followers in certain range
// 	 * 
// 	 * @param uid
// 	 * @param range
// 	 * @return
// 	 */
// 	public static Set<String> getFollowers(String uid, Range range) {
// 		return getUids(KeyUtils.followers(uid), range);
// 	}

// 	/**
// 	 * Get all the followers
// 	 * 
// 	 * @param uid
// 	 * @return
// 	 */
// 	public static Set<String> getFollowers(String uid) {
// 		return getUids(KeyUtils.followers(uid), new Range(0, -1));
// 	}

// 	/**
// 	 * Get my following user id list in certain range
// 	 * 
// 	 * @param uid
// 	 * @param range
// 	 * @return
// 	 */
// 	public static Set<String> getFollowing(String uid, Range range) {
// 		return getUids(KeyUtils.following(uid), range);
// 	}

// 	/**
// 	 * Get all the following
// 	 * 
// 	 * @param uid
// 	 * @return
// 	 */
// 	public static Set<String> getFollowing(String uid) {
// 		return getUids(KeyUtils.following(uid), new Range(0, -1));
// 	}

// 	@Deprecated
// 	public static Set<String> getFollowersNames(String uid) {
// 		return covertUidsToNames(KeyUtils.followers(uid));
// 	}

// 	@Deprecated
// 	public static Set<String> getFollowingNames(String uid) {
// 		return covertUidsToNames(KeyUtils.following(uid));
// 	}

// 	/**
// 	 * Whether uid is following targetUid
// 	 * 
// 	 * @param uid
// 	 * @param targetUid
// 	 * @return
// 	 */
// 	public static boolean isFollowing(String uid, String targetUid) {
// 		Jedis redis = null;
// 		boolean broken = false;
// 		try {
// 			redis = pool.getResource();
// 			return redis.sismember(KeyUtils.following(uid), targetUid);
// 			// Long r = redis.zrank(KeyUtils.following(uid), targetUid);
// 			// if (r == null)
// 			// return false;
// 			// return true;
// 		} catch (JedisConnectionException e) {
// 			broken = true;
// 			logger.severe("Error when handling Redis! " + e.getMessage());
// 		} finally {
// 			returnJedis(redis, broken);
// 		}
// 		return false;

// 	}

// 	/**
// 	 * Whether uid is the fans of targetUid
// 	 * 
// 	 * @param uid
// 	 * @param targetUid
// 	 * @return
// 	 */
// 	public static boolean isFollower(String uid, String targetUid) {
// 		return redis.sismember(KeyUtils.followers(targetUid), uid);
// 		// Long r = redis.zrank(KeyUtils.followers(targetUid), uid);
// 		// if (r == null)
// 		// return false;
// 		// return true;
// 	}

// 	@Deprecated
// 	public static boolean follow(String targetUser) {
// 		String targetUid = findUid(targetUser);

// 		long r1 = following(UserSecurity.getUid(), targetUid);
// 		long r2 = followers(targetUid, UserSecurity.getUid());

// 		if (r1 == 1 && r2 == 1)
// 			return true;
// 		return false;
// 	}

// 	/**
// 	 * Add follow relationship
// 	 * 
// 	 * @param userId
// 	 * @param targetId
// 	 * @return
// 	 */
// 	public static boolean follow(String userId, String targetId) {

// 		long r1 = following(userId, targetId);
// 		long r2 = followers(targetId, userId);

// 		if (r1 == 1l && r2 == 1l)
// 			return true;
// 		return false;
// 	}

// 	@Deprecated
// 	public static boolean stopFollowing(String targetUser) {
// 		String targetUid = findUid(targetUser);

// 		long r1 = unfollowing(UserSecurity.getUid(), targetUid);
// 		long r2 = unfollowers(targetUid, UserSecurity.getUid());

// 		if (r1 == 1 && r2 == 1)
// 			return true;
// 		return false;
// 	}

// 	/**
// 	 * Remove follow relationship
// 	 * 
// 	 * @param userId
// 	 * @param targetId
// 	 * @return
// 	 */
// 	public static boolean stopFollowing(String userId, String targetId) {

// 		long r1 = unfollowing(userId, targetId);
// 		long r2 = unfollowers(targetId, userId);

// 		if (r1 == 1 && r2 == 1)
// 			return true;
// 		return false;
// 	}

// 	/**
// 	 * Add the following sorted set, with timestamp as score
// 	 * 
// 	 * @param uid
// 	 * @param targetId
// 	 * @return
// 	 */
// 	private static long following(String uid, String targetId) {
// 		return redis.sadd(KeyUtils.following(uid), targetId);
// 		// return redis.zadd(KeyUtils.following(uid),
// 		// System.currentTimeMillis(),
// 		// targetId);
// 	}

// 	/**
// 	 * Add the follower sorted set, with timestamp as score
// 	 * 
// 	 * @param targetUid
// 	 * @param uid
// 	 * @return
// 	 */
// 	private static Long followers(String targetUid, String uid) {
// 		return redis.sadd(KeyUtils.followers(targetUid), uid);
// 		// return redis.zadd(KeyUtils.followers(targetUid),
// 		// System.currentTimeMillis(), uid);
// 	}

// 	/**
// 	 * Remove from the following sorted set
// 	 * 
// 	 * @param uid
// 	 * @param targetId
// 	 * @return
// 	 */
// 	private static long unfollowing(String uid, String targetId) {
// 		// return redis.zrem(KeyUtils.following(uid), targetId);
// 		return redis.srem(KeyUtils.following(uid), targetId);
// 	}

// 	/**
// 	 * Remove from the follower sorted set
// 	 * 
// 	 * @param targetUid
// 	 * @param uid
// 	 * @return
// 	 */
// 	private static Long unfollowers(String targetUid, String uid) {
// 		// return redis.zrem(KeyUtils.followers(targetUid), uid);
// 		return redis.srem(KeyUtils.followers(targetUid), uid);
// 	}

// 	/**
// 	 * Get a certain share with pid
// 	 * 
// 	 * @param pid
// 	 * @return
// 	 */
// 	public static Share getShare(String pid) {

// 		HashMap<String, String> map = new HashMap<String, String>();

// 		map.put(sid, pid);
// 		map.put(uid, redis.hget(KeyUtils.post(pid), uid));
// 		map.put(name, redis.hget(KeyUtils.post(pid), name));
// 		map.put(icon, redis.hget(KeyUtils.post(pid), icon));
// 		map.put(title, redis.hget(KeyUtils.post(pid), title));
// 		map.put(content, redis.hget(KeyUtils.post(pid), content));
// 		map.put(time, redis.hget(KeyUtils.post(pid), time));
// 		map.put(like, redis.hget(KeyUtils.post(pid), like));
// 		map.put(img, redis.hget(KeyUtils.post(pid), img));
// 		map.put(link, redis.hget(KeyUtils.post(pid), link));

// 		return convertShare(pid, map);
// 	}

// 	/**
// 	 * 
// 	 * @param cid
// 	 * @param pid
// 	 * @return
// 	 */
// 	public static Comment getComment(String cid, String pid) {

// 		Comment comment = new Comment();

// 		String tcid = redis.hget(KeyUtils.comment(pid) + cid, cid);
// 		String tuid = redis.hget(KeyUtils.comment(pid) + cid, uid);
// 		String tname = redis.hget(KeyUtils.comment(pid) + cid, name);
// 		String ticon = redis.hget(KeyUtils.comment(pid) + cid, icon);
// 		String tcontent = redis.hget(KeyUtils.comment(pid) + cid, content);
// 		String treplyUid = redis.hget(KeyUtils.comment(pid) + cid, replyUid);
// 		String treplyPid = redis.hget(KeyUtils.comment(pid) + cid, replyPid);
// 		String trootShare = redis.hget(KeyUtils.comment(pid) + cid, rootShare);
// 		String ttime = redis.hget(KeyUtils.comment(pid) + cid, time);

// 		comment.setCid(tcid);
// 		comment.setUid(tuid);
// 		comment.setUserName(tname);
// 		comment.setUserIcon(ticon);
// 		comment.setContent(tcontent);
// 		comment.setReplyUid(treplyUid);
// 		comment.setReplyPid(treplyPid);
// 		comment.setRootShare(trootShare);
// 		comment.setTime(ttime);
// 		logger.info("Content : " + comment.getContent() + " \n uid : "
// 				+ comment.getUid() + " \n name : " + comment.getUserName()
// 				+ " \n Time : " + comment.getTime() + " \n ReplyPid : "
// 				+ comment.getReplyPid() + " \n ReplyUid : "
// 				+ comment.getReplyUid() + " \n RootShare : "
// 				+ comment.getRootShare());

// 		return comment;
// 	}

// 	/**
// 	 * Get comment list for a certain share : pid
// 	 * 
// 	 * @param pid
// 	 * @param range
// 	 *            : pagination
// 	 * @return
// 	 */
// 	public static List<Comment> getComments(String pid, Range range) {

// 		List<Comment> comments = new ArrayList<Comment>();

// 		Set<String> list = redis.zrevrange(KeyUtils.comments(pid), range.begin,
// 				range.end);

// 		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
// 			String cid = iterator.next();
// 			Comment comment = new Comment();

// 			String tcid = redis.hget(KeyUtils.comment(pid) + cid, cid);
// 			String tuid = redis.hget(KeyUtils.comment(pid) + cid, uid);
// 			String tname = redis.hget(KeyUtils.comment(pid) + cid, name);
// 			String ticon = redis.hget(KeyUtils.comment(pid) + cid, icon);
// 			String tcontent = redis.hget(KeyUtils.comment(pid) + cid, content);
// 			String treplyUid = redis
// 					.hget(KeyUtils.comment(pid) + cid, replyUid);
// 			String treplyPid = redis
// 					.hget(KeyUtils.comment(pid) + cid, replyPid);
// 			String trootShare = redis.hget(KeyUtils.comment(pid) + cid,
// 					rootShare);
// 			String ttime = redis.hget(KeyUtils.comment(pid) + cid, time);

// 			comment.setCid(tcid);
// 			comment.setUid(tuid);
// 			comment.setUserName(tname);
// 			comment.setUserIcon(ticon);
// 			comment.setContent(tcontent);
// 			comment.setReplyUid(treplyUid);
// 			comment.setReplyPid(treplyPid);
// 			comment.setRootShare(trootShare);
// 			comment.setTime(ttime);
// 			logger.info("Content : " + comment.getContent() + " \n uid : "
// 					+ comment.getUid() + " \n name : " + comment.getUserName()
// 					+ " \n Time : " + comment.getTime() + " \n ReplyPid : "
// 					+ comment.getReplyPid() + " \n ReplyUid : "
// 					+ comment.getReplyUid() + " \n RootShare : "
// 					+ comment.getRootShare());

// 			comments.add(comment);
// 		}

// 		return comments;
// 	}

// 	/**
// 	 * Get the share list with certain user : uid
// 	 * 
// 	 * @param uid
// 	 * @param range
// 	 *            : pagination
// 	 * @return
// 	 */
// 	public static List<Share> getShares(String uid, Range range) {
// 		List<Share> list = convertPidsToShares(KeyUtils.posts(uid), range);
// 		return list;
// 	}

// 	/**
// 	 * Get the shares with ids
// 	 * 
// 	 * @param key
// 	 * @param range
// 	 *            : pagination
// 	 * @return
// 	 */
// 	private static List<Share> convertPidsToShares(String key, Range range) {

// 		LinkedList<Share> shares = new LinkedList<Share>();
// 		logger.info("convertPidsToShares " + key);
// 		// List<String> sharePids = redis.lrange(key, range.begin, range.end);
// 		Set<String> sharePids = redis.zrevrange(key, range.begin, range.end);
// 		for (Iterator<String> iterator = sharePids.iterator(); iterator
// 				.hasNext();) {
// 			String pid = iterator.next();
// 			HashMap<String, String> map = new HashMap<String, String>();
// 			map.put(sid, pid);
// 			map.put(uid, redis.hget(KeyUtils.post(pid), uid));
// 			map.put(name, redis.hget(KeyUtils.post(pid), name));
// 			map.put(icon, redis.hget(KeyUtils.post(pid), icon));
// 			map.put(title, redis.hget(KeyUtils.post(pid), title));
// 			map.put(content, redis.hget(KeyUtils.post(pid), content));
// 			map.put(time, redis.hget(KeyUtils.post(pid), time));
// 			map.put(like, redis.hget(KeyUtils.post(pid), like));
// 			map.put(img, redis.hget(KeyUtils.post(pid), img));
// 			map.put(link, redis.hget(KeyUtils.post(pid), link));
// 			shares.addLast(convertShare(pid, map));
// 		}

// 		return shares;
// 	}

// 	/**
// 	 * 
// 	 * @param pid
// 	 * @param hash
// 	 * @return
// 	 */
// 	private static Share convertShare(String pid, Map<String, String> hash) {
// 		Share share = new Share();
// 		share.setTitle(hash.get(title));
// 		share.setContent(hash.get(content));
// 		share.setTime(hash.get(time));
// 		share.setUid(hash.get(uid));
// 		share.setUserName(hash.get(name));
// 		share.setUserIcon(hash.get(icon));
// 		share.setSid(pid);
// 		// share.setTimeArg(hash.get("time"));
// 		share.setLike(Integer.parseInt(hash.get(like) == null ? "0" : hash
// 				.get(like)));
// 		// handle img array
// 		String imgPaths = hash.get(img);
// 		if (imgPaths != null) {
// 			share.setImg(imgPaths.split("@"));
// 		}

// 		share.setLink(hash.get(link));
// 		return share;
// 	}

// 	/**
// 	 * 
// 	 * @param pid
// 	 * @param hash
// 	 * @return
// 	 */
// 	private static Comment convertComment(String pid, Map<String, String> hash) {

// 		Comment comment = new Comment();
// 		comment.setCid(hash.get(cid));
// 		comment.setUid(hash.get(uid));
// 		comment.setUserName(hash.get(name));
// 		comment.setUserIcon(hash.get(icon));
// 		comment.setContent(hash.get(content));
// 		comment.setReplyUid(hash.get(replyUid));
// 		comment.setReplyPid(hash.get(replyPid));
// 		comment.setRootShare(hash.get(rootShare));
// 		comment.setTime(hash.get(time));
// 		comment.setTimeArg(hash.get(time));

// 		return comment;
// 	}

// 	/**
// 	 * 
// 	 * @param pid
// 	 * @return
// 	 */
// 	public static boolean isShareValid(String pid) {
// 		String exist = redis.get(KeyUtils.post(pid));
// 		return exist == null ? false : true;
// 	}

// 	/**
// 	 * 
// 	 * @param targetUid
// 	 * @param range
// 	 * @return
// 	 */
// 	public static boolean hasMoreShares(String targetUid, Range range) {
// 		return convertPidsToShares(targetUid, range).size() > range.end + 1;
// 	}

// 	/**
// 	 * Add like number for certain resource
// 	 * 
// 	 * @param resourceId
// 	 * @return
// 	 */
// 	public static long like(String resourceId) {
// 		return redis.incr(KeyUtils.like(resourceId));
// 	}

// 	/**
// 	 * Get the new share number since last read
// 	 * 
// 	 * @param uid
// 	 * @param targetId
// 	 * @param curTime
// 	 * @return
// 	 */
// 	public static long getNewShareNum(String uid, String targetId, long curTime) {
// 		String lastRead = redis.get(KeyUtils.lastRead(uid, targetId));
// 		logger.info("Last read : " + lastRead);
// 		long num = 0;
// 		if (lastRead == null || lastRead.equalsIgnoreCase("")) {
// 			// No lastRead, means first read, get all
// 			logger.info(KeyUtils.timeline(targetId) + " range : " + 0 + " - "
// 					+ curTime);
// 			num = redis.zcount(KeyUtils.timeline(targetId), 0, curTime);
// 			// Set the lastRead flag
// 			redis.set(KeyUtils.lastRead(uid, targetId), "" + curTime);
// 			logger.info(KeyUtils.lastRead(uid, targetId) + " last read : "
// 					+ curTime);
// 		} else {
// 			// Have lastRead flag, get the num between lastRead and curTime
// 			num = redis.zcount(KeyUtils.timeline(targetId),
// 					Long.parseLong(lastRead), curTime);
// 			// Update the lastRead flag
// 			lastRead = redis.getSet(KeyUtils.lastRead(uid, targetId), ""
// 					+ curTime);
// 			logger.info(KeyUtils.lastRead(uid, targetId) + " update : old"
// 					+ lastRead + " - new : " + curTime);
// 		}
// 		return num;
// 	}

// 	/**
// 	 * Delete a share
// 	 * 
// 	 * @param uid
// 	 * @param pid
// 	 * @return
// 	 */
// 	public static boolean deletePost(String uid, String pid) {
// 		// Share s = RedisCache.getShare(pid);
// 		// if (s != null) {
// 		// String v = s.getUid();
// 		// if (v != null && v.equals(uid)) {
// 		redis.hdel(KeyUtils.post(pid), uid);
// 		redis.hdel(KeyUtils.post(pid), content);
// 		redis.hdel(KeyUtils.post(pid), title);
// 		redis.hdel(KeyUtils.post(pid), name);
// 		redis.hdel(KeyUtils.post(pid), icon);
// 		redis.hdel(KeyUtils.post(pid), time);
// 		redis.hdel(KeyUtils.post(pid), img);
// 		redis.hdel(KeyUtils.post(pid), link);
// 		redis.hdel(KeyUtils.post(pid), like);
// 		redis.hdel(KeyUtils.post(pid), sid);

// 		// Delete links
// 		Long l1 = redis.zrem(KeyUtils.posts(uid), pid);
// 		// Delete timeline, if no following, identical to posts list
// 		Long l2 = redis.zrem(KeyUtils.timeline(uid), pid);

// 		if (l1 == 1l && l2 == 1l)
// 			return true;

// 		// }
// 		// }
// 		return false;
// 	}

// 	/**
// 	 * Delete a comment
// 	 * 
// 	 * @param cid
// 	 * @param sid
// 	 * @param shareOwnerId
// 	 * @return
// 	 */
// 	public static boolean deleteComment(String uid, String cid, String sid,
// 			String shareOwnerId) {
// 		logger.info("Delete comment - userId : " + uid + " commentId : " + cid
// 				+ " shareId : " + sid + " shareOwner : " + shareOwnerId);
// 		// Share s = RedisCache.getShare(sid);
// 		// if (s != null) {
// 		// String v = s.getUid();
// 		// logger.info("Query share uid : " + v);

// 		// Share owner want to del comment
// 		// if (v != null && v.equals(uid)) {
// 		// Comment owner want to del comment
// 		// || uid.equals(shareOwnerId)) {
// 		// Delete comment info
// 		// Comment c = RedisCache.getComment(cid, sid);
// 		// if (c != null) {
// 		// String id = c.getUid();
// 		// if (id != null && id.equals(uid)) {
// 		redis.hdel(KeyUtils.comment(sid) + cid, uid);
// 		redis.hdel(KeyUtils.comment(sid) + cid, name);
// 		redis.hdel(KeyUtils.comment(sid) + cid, icon);
// 		redis.hdel(KeyUtils.comment(sid) + cid, cid);
// 		redis.hdel(KeyUtils.comment(sid) + cid, content);
// 		redis.hdel(KeyUtils.comment(sid) + cid, time);
// 		redis.hdel(KeyUtils.comment(sid) + cid, replyPid);
// 		redis.hdel(KeyUtils.comment(sid) + cid, replyUid);
// 		redis.hdel(KeyUtils.comment(sid) + cid, rootShare);

// 		// Delete comment id from share's comment list
// 		Long l = redis.zrem(KeyUtils.comments(sid), cid);
// 		if (l == 1l)
// 			return true;
// 		// }
// 		// }
// 		return false;
// 	}

// 	/**
// 	 * Post a comment
// 	 * 
// 	 * @param comment
// 	 * @return
// 	 */
// 	public static String postComment(Comment comment) {

// 		String cid = String.valueOf(redis.incr("global:pid"));
// 		logger.info("cid : " + cid);

// 		// add comment
// 		HashMap<String, String> commentMap = new HashMap<String, String>();
// 		commentMap.put(cid, cid);
// 		commentMap.put(content,
// 				comment.getContent() == null ? "" : comment.getContent());
// 		commentMap.put(uid, comment.getUid() == null ? "" : comment.getUid());
// 		commentMap.put(name,
// 				comment.getUserName() == null ? "" : comment.getUserName());
// 		commentMap.put(icon,
// 				comment.getUserIcon() == null ? "" : comment.getUserIcon());
// 		commentMap
// 				.put(time, comment.getTime() == null ? "" : comment.getTime());
// 		commentMap.put(replyPid,
// 				comment.getReplyPid() == null ? "" : comment.getReplyPid());
// 		commentMap.put(replyUid,
// 				comment.getReplyUid() == null ? "" : comment.getReplyUid());
// 		commentMap.put(rootShare,
// 				comment.getRootShare() == null ? "" : comment.getRootShare());

// 		logger.info("Content : " + comment.getContent() + " \n uid : "
// 				+ comment.getUid() + " \n name : " + comment.getUserName()
// 				+ " \n Time : " + comment.getTime() + " \n ReplyPid : "
// 				+ comment.getReplyPid() + " \n ReplyUid : "
// 				+ comment.getReplyUid() + " \n RootShare : "
// 				+ comment.getRootShare());

// 		// Add comment info
// 		redis.hmset(KeyUtils.comment(comment.getRootShare()) + cid, commentMap);

// 		// Add comment id to share's comment list
// 		redis.zadd(KeyUtils.comments(comment.getRootShare()),
// 				Double.parseDouble(comment.getTime()), cid);

// 		return cid;
// 	}

// 	@Deprecated
// 	public static String post(String username, Feed feed) {
// 		Share share = feed.getShare();

// 		String uid = findUid(username);
// 		share.setUid(uid);

// 		String pid = String.valueOf(redis.incr("global:pid"));
// 		logger.info("pid : " + pid);

// 		// add post
// 		// post(pid).putAll(postMapper.toHash(share));
// 		HashMap<String, String> shareMap = new HashMap<String, String>();
// 		shareMap.put(content, share.getContent());
// 		shareMap.put(uid, share.getUid());
// 		shareMap.put(time, share.getTime());

// 		logger.info("Content : " + share.getContent() + " /n uid : "
// 				+ share.getUid() + " /n Time : " + share.getTime());

// 		redis.hmset(KeyUtils.post(pid), shareMap);

// 		long curTime = System.currentTimeMillis();
// 		// add links
// 		// redis.lpush(KeyUtils.posts(uid), pid);
// 		redis.zadd(KeyUtils.posts(uid), curTime, pid);
// 		// add timeline, if no following, identical to posts list
// 		// redis.lpush(KeyUtils.timeline(uid), pid);
// 		redis.zadd(KeyUtils.timeline(uid), curTime, pid);

// 		// update followers
// 		for (String follower : getFollowers(uid)) {
// 			// redis.lpush(KeyUtils.timeline(findUid(follower)), pid);
// 			redis.zadd(KeyUtils.timeline(findUid(follower)), curTime, pid);
// 		}

// 		return pid;
// 		// handleMentions(share, pid, replyName);
// 	}

// 	// Post a share
// 	/**
// 	 * Post a share and update the followers's sorted set and group's sorted set
// 	 * 
// 	 * @param uid
// 	 * @param feed
// 	 * @param followers
// 	 * @param groups
// 	 * @return
// 	 */
// 	public static String post(String uid, Feed feed, List<String> followers,
// 			List<String> groups) {
// 		Share share = feed.getShare();

// 		String pid = String.valueOf(redis.incr("global:pid"));
// 		logger.info("pid : " + pid);

// 		// add post
// 		HashMap<String, String> shareMap = new HashMap<String, String>();
// 		shareMap.put(title, share.getTitle() == null ? "" : share.getTitle());
// 		shareMap.put(content,
// 				share.getContent() == null ? "" : share.getContent());
// 		shareMap.put(uid, share.getUid() == null ? "" : share.getUid());
// 		shareMap.put(name,
// 				share.getUserName() == null ? "" : share.getUserName());
// 		shareMap.put(icon,
// 				share.getUserIcon() == null ? "" : share.getUserIcon());
// 		shareMap.put(time, share.getTime() == null ? "" : share.getTime());
// 		// handle imgs
// 		String[] imgs = share.getImg();
// 		String imgPaths = "";
// 		if (imgs == null) {
// 			logger.info("No img!");
// 		} else {
// 			for (int i = 0; i < imgs.length; i++) {
// 				imgPaths += imgs[i] + "@";
// 			}
// 			if (imgs.length > 4)
// 				logger.info("Error! img number support limit 4, now it's "
// 						+ imgs.length);
// 		}
// 		shareMap.put(img, imgPaths == null ? "" : imgPaths);
// 		shareMap.put(link, share.getLink() == null ? "" : share.getLink());
// 		shareMap.put(like,
// 				"" + share.getLike() == null ? "" : "" + share.getLike());
// 		shareMap.put(sid, share.getSid() == null ? "" : share.getSid());

// 		logger.info("Content : " + share.getContent() + " /n uid : "
// 				+ share.getUid() + " /n Time : " + share.getTime());

// 		redis.hmset(KeyUtils.post(pid), shareMap);

// 		long curTime = System.currentTimeMillis();
// 		// add links
// 		redis.zadd(KeyUtils.posts(uid), curTime, pid);
// 		// add timeline, if no following, identical to posts list
// 		redis.zadd(KeyUtils.timeline(uid), curTime, pid);

// 		// update followers
// 		for (String followerId : followers) {
// 			// redis.lpush(KeyUtils.timeline(findUid(follower)), pid);
// 			redis.zadd(KeyUtils.timeline(followerId), curTime, pid);
// 		}

// 		// update groups
// 		for (String groupId : groups) {
// 			// redis.lpush(KeyUtils.timeline(findUid(follower)), pid);
// 			redis.zadd(KeyUtils.timeline(groupId), curTime, pid);
// 		}

// 		return pid;
// 		// handleMentions(share, pid, replyName);
// 	}

// 	// private static void handleMentions(Share share, String pid, String name)
// 	// {
// 	// // find mentions
// 	// Collection<String> mentions = findMentions(share.getContent());
// 	//
// 	// for (String mention : mentions) {
// 	// String uid = findUid(mention);
// 	// if (uid != null) {
// 	// // mentions(uid).addFirst(pid);
// 	// mentions(uid).add(pid);
// 	// }
// 	// }
// 	// }

// 	@Deprecated
// 	public static boolean hasMoreTimeline(String targetUid, Range range) {
// 		return timeline(targetUid, range).size() > range.end + 1;
// 	}

// 	@Deprecated
// 	public static boolean hasMoreTimeline(Range range) {
// 		return timeline.size() > range.end + 1;
// 	}

// 	/**
// 	 * Get certain user's timeline with post in range
// 	 * 
// 	 * @param uid
// 	 * @param range
// 	 * @return
// 	 */
// 	public static List<Share> timeline(String uid, Range range) {
// 		return convertPidsToShares(KeyUtils.timeline(uid), range);
// 	}

// 	/**
// 	 * Get certain user's timeline with post in range
// 	 * 
// 	 * @param uid
// 	 * @param range
// 	 * @return
// 	 */
// 	public static List<Share> getTimeline(String uid, Range range) {
// 		return convertPidsToShares(KeyUtils.timeline(uid), range);
// 	}

// 	public static String getLatestMsg(String uid) {
// 		List<Share> shares = convertPidsToShares(KeyUtils.timeline(uid),
// 				new Range(0, 1));
// 		if (shares == null || shares.size() == 0) {
// 			return "";
// 		} else {
// 			Share share = shares.get(0);
// 			if (share == null) {
// 				return "";
// 			} else {
// 				return share.getContent();
// 			}
// 		}
// 	}

// 	// public static List<Feed> getMentions(String uid, Range range) {
// 	// return convertPidsToShares(KeyUtils.mentions(uid), range);
// 	//
// 	// }

// 	// private static List<String> timeline(String uid) {
// 	// redis.lpop(KeyUtils.timeline(uid));
// 	// return new ArrayList<String>();
// 	// }

// 	// private static Set<String> mentions(String uid) {
// 	// return redis.smembers(KeyUtils.mentions(uid));
// 	// }

// 	// private static Map<String, String> post(String pid) {
// 	// // return new HashMap<String, String>(KeyUtils.post(pid));
// 	// return new HashMap();
// 	// }
// 	//
// 	// private static List<String> posts(String uid) {
// 	// // return new ArrayList<String>(KeyUtils.posts(uid));
// 	// return new ArrayList<String>();
// 	// }

// 	// various util methods

// 	// private static String replaceReplies(String content) {
// 	// Matcher regexMatcher = MENTION_REGEX.matcher(content);
// 	// while (regexMatcher.find()) {
// 	// String match = regexMatcher.group();
// 	// int start = regexMatcher.start();
// 	// int stop = regexMatcher.end();
// 	//
// 	// String uName = match.substring(1);
// 	// if (isUserValid(uName)) {
// 	// content = content.substring(0, start) + "<a href=" + uName
// 	// + "\">" + match + "</a>" + content.substring(stop);
// 	// }
// 	// }
// 	// return content;
// 	// }

// 	// public static Collection<String> findMentions(String content) {
// 	// Matcher regexMatcher = MENTION_REGEX.matcher(content);
// 	// List<String> mentions = new ArrayList<String>(4);
// 	//
// 	// while (regexMatcher.find()) {
// 	// mentions.add(regexMatcher.group().substring(1));
// 	// }
// 	//
// 	// return mentions;
// 	// }

// 	public static void main(String[] args) {

// 	}
// }
