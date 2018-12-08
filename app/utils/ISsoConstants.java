/*
* $RCSfile$
* $Revision$
* $Date$
* (c) dasreda.ru, 2017
*/
package utils;

/**
 * Interface {@link ISsoConstants} defines the common constants for
 * 
 * @author Veremeichik
 */
public interface ISsoConstants {	
	Long FAKE_USERID = Long.valueOf("1");
	
	String JWT_CLAIM_APPS = "apps";
	String JWT_CLAIM_FIRST_NAME = "firstName";
	String JWT_CLAIM_ID = "id";
	String JWT_CLAIM_LAST_NAME = "lastName";
	String JWT_CLAIM_MIDLLE_NAME = "middleName";
	String JWT_CLAIM_PHONE = "phone";
	String JWT_CLAIM_ROLES = "roles";
	String JWT_CLAIM_SUB = "sub";
	String JWT_CLAIM_USERNAME = "username";	
}