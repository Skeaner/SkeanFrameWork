package skean.me.base.db;

import skean.me.base.utils.ContentUtil;

/**
 * DAO基类
 */
public class BaseDAO {

    public static String wrapKeyword(String keyword) {
        return ContentUtil.concat("%", keyword, "%");
    }
}
