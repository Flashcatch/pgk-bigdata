/*
* $RCSfile$
* $Revision$
* $Date$
* (c) dasreda.ru, 2017
*/
package utils;

/**
 * Interface {@link IAppConstants} defines the application constants
 * 
 * @author Veremeichik
 */
public interface IAppConstants extends ISsoConstants {

	/** Constant defines the name of application module registered */
	String APP_MODULE_NAME = "uni-blog";

	String APP_NAME = "blog";
	
	String CFG_API_APP_MODULE_NAME = "api.app_module.name";
	String CFG_API_AUTHOR_ROLE_NAME = "api.role_author.name";
	String CFG_API_COREROUTER_BY_ROLE = "api.core_router.by_role";
	String CFG_API_COREROUTER_CATEGORIES = "api.core_router.categories";
	String CFG_API_COREROUTER_URL = "api.core_router.url";
	String CFG_API_UNISTORAGE_URL = "api.uni_storage.url";
	
	String CFG_SSO_ISSUER = "sso.issuer";
	String CFG_SSO_JWT_COOKIE = "sso.jwt_cookie_name";
	
	String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
	
	String EMPTY = "";
	String COMMA = ","; //$NON-NLS-1$
	String COMMON_DICTIONARY_CATEGORIES = "common.dictionary.categories";
}