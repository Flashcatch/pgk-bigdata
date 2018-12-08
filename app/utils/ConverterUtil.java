/*
* $RCSfile$
* $Revision$
* $Date$
* (c) dasreda.ru, 2017
*/
package utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.exceptions.ExecutionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class {@link ConverterUtil} provides methods to convert list, arrays to a
 * string and vice versa
 *
 * @author Veremeichik
 */
public abstract class ConverterUtil {
	/**
	 * Creates the string delimited with comma from a list. If the array is
	 * empty or <code>null</code> returns empty
	 *
	 * @param aList
	 *            Object array
	 *
	 * @return {@link String} like Zert1,Zert2
	 */
	public static String asCommaListString(List<?> aList) {
		StringBuffer buf = new StringBuffer();
		if (aList != null) {
			int i = 0;
			for (Object object : aList) {
				if (i > 0) {
					buf.append(IAppConstants.COMMA);
				}
				buf.append(object.toString());
				i++;
			}
		}
		return buf.toString();
	}

	/**
	 * Creates the string delimited with comma from an array. If the array is
	 * empty or <code>null</code> returns empty String
	 *
	 * @param anArray
	 *            Object array
	 * @return {@link String} like (Zert1,Zert2)
	 */
	public static String asCommaListString(Object[] anArray) {
		StringBuffer buf = new StringBuffer();
		if ((anArray != null) && (anArray.length > 0)) {
			for (int i = 0; i < anArray.length; i++) {
				if (i > 0) {
					buf.append(IAppConstants.COMMA);
				}
				buf.append(anArray[i].toString());
			}
		}
		return buf.toString();
	}

	/**
	 * Check whether string is null and return value as {@link Integer}
	 *
	 * @param aValue
	 * @return
	 */
	public static Integer asInteger(String aValue) {
		if (aValue == null) {
			return null;
		}
		return Integer.valueOf(aValue);
	}

	/**
	 * Check whether string is null and return value as {@link Integer} with
	 * default
	 *
	 * @param aValue
	 * @param aDefault
	 * @return
	 */
	public static Integer asInteger(String aValue, int aDefault) {
		if (aValue == null) {
			return Integer.valueOf(aDefault);
		}
		return Integer.valueOf(aValue);
	}

	/**
	 * Creates the list of {@link Long} from the comma-delimited string
	 *
	 * @param aSource
	 * @param aDefault
	 * @return
	 */
	public static List<Long> asListOfLongs(String aSource, List<Long> aDefault) {
		if (aSource == null) {
			return aDefault == null ? null : new ArrayList<>(aDefault);
		}
		return Stream.of(aSource.split(IAppConstants.COMMA)).map(Long::parseLong).collect(Collectors.toList());
	}

	/**
	 * Creates the list of strings from the comma-delimited string
	 *
	 * @param aSource
	 * @param aDefault
	 * @return
	 */
	public static List<String> asListOfStrings(String aSource, List<String> aDefault) {
		if (aSource == null) {
			return aDefault == null ? null : new ArrayList<>(aDefault);
		}
		return Arrays.asList(aSource.split(IAppConstants.COMMA));
	}

	/**
	 * Check whether string is null and return value as {@link Long}
	 *
	 * @param aValue
	 * @return
	 */
	public static Long asLong(String aValue) {
		if (aValue == null) {
			return null;
		}
		return Long.valueOf(aValue);
	}

	/**
	 * Check whether string is null and return value as {@link Long} with a
	 * default value
	 *
	 * @param aValue
	 * @param aDefault
	 * @return
	 */
	public static Long asLong(String aValue, long aDefault) {
		if (aValue == null) {
			return Long.valueOf(aDefault);
		}
		return Long.valueOf(aValue);
	}

	/**
	 * Do the given Objects equal ? This is true, if both are null or if both
	 * are not null and anOldVal.equals(aNewVal) <br>
	 * NOTE: this is a convenience method for not having to mess around with
	 * null-checks when comparing two Objects.
	 *
	 * @param anOldVal
	 *            Object that may be null
	 * @param aNewVal
	 *            Object that may be null
	 * @return
	 */
	public static boolean equals(Object anOldVal, Object aNewVal) {
		return ((anOldVal == null) & (aNewVal == null)) | ((anOldVal != null) && anOldVal.equals(aNewVal));
	}

	/**
	 * Gets List of objects from json string
	 *
	 * @param responseString data for convertion
	 * @param dataClass      required class
	 * @param serviceName    name of service to show in error message
	 * @return List of dataClass
	 */
	public static <T> List<T> getListfromResponse(String responseString, Class<T> dataClass, String serviceName) {
		ObjectMapper mapper = new ObjectMapper();

		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, dataClass);
		try {
			return mapper.readValue(responseString, type);


		} catch (IOException e) {
			throw ExecutionException.createExecutionException("error of processsing response from " + serviceName);
		}
	}

	/**
	 * Gets List of objects from json string
	 *
	 * @param responseString data for convertion
	 * @param dataClass      required class
	 * @return List of dataClass
	 */
	public static <T> List<T> getListfromResponse(String responseString, Class<T> dataClass) {
		return getListfromResponse(responseString, dataClass, "unknown ");

	}
}