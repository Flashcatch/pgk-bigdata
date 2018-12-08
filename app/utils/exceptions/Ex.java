package utils.exceptions;

import utils.entity.Errors;

import java.util.List;
import java.util.Map;

/**
 * Created by Pavel Dudin
 * on 11.11.2017
 * padudin@dasreda.ru
 */
public class Ex {
    
    public static class Request {
        
        public static class Body {

            public static NotValidParameterException createEmptyBodyException() {
                return NotValidParameterException.createEmptyBodyException();
            }

            public static NotValidParameterException createInvalidJson() {
                return NotValidParameterException.createInvalidJson();
            }

            public static class Params {

                public static NotValidParameterException createUserFieldIsNull(String fieldName) {
                    return NotValidParameterException.createUserFieldIsNull(fieldName);
                }

                public static NotValidParameterException createIdIsNull() {
                    return NotValidParameterException.createIdIsNull();
                }

                public static NotValidParameterException createIdIsNull(String fieldName) {
                    return NotValidParameterException.createIdIsNull(fieldName);
                }

                public static NotValidParameterException createCustomFieldIsNull(String fieldName) {
                    return NotValidParameterException.createCustomFieldIsNull(fieldName);
                }

                public static NotValidParameterException createNotValidParameter(String name, String value, String jsonMessage) {
                    return NotValidParameterException.createNotValidParameter(name, value, jsonMessage);
                }

                public static EntityAlreadyExistException createSlugExist(String slug) {
                    return EntityAlreadyExistException.createSlugExist(slug);
                }

                public static RequiredObjectNotFoundException createObjectNotFoundException(final String objectName) {
                    return RequiredObjectNotFoundException.createObjectNotFoundException(objectName);
                }
                
                public static NotValidParameterException createNotValidFields(String logMessage, Map<String, List<String>> map) {
                    return NotValidParameterException.createNotValidFields(logMessage, map);
                }

                public static NotValidParameterException createNotValidFields(String logMessage, Errors errors) {
                    return NotValidParameterException.createNotValidFields(logMessage, errors);
                }
                
            }

        }

        public static class Query {

            public static NotValidParameterException createNotValidQueryParameter(String name, String value) {
                return NotValidParameterException.createNotValidQueryParameter(name, value);
            }

            public static NotValidParameterException createNotValidFields(String logMessage, Map<String, List<String>> map) {
                return NotValidParameterException.createNotValidFields(logMessage, map);
            }

            public static NotValidParameterException createNotValidFields(String logMessage, Errors errors) {
                return NotValidParameterException.createNotValidFields(logMessage, errors);
            }

        }

        public static WrongMethodException createWrongMethodException(String used, String ... allowedMethods) {
            return WrongMethodException.createWrongMethodException(used, allowedMethods);
        }
        
    }
    
    public static class Entity {

        public static EntityAlreadyExistException createSlugExist(String slug) {
            return EntityAlreadyExistException.createSlugExist(slug);
        }

        public static EntityExpiredException createEntityExpired() {
            return EntityExpiredException.createEntityExpired();
        }

        public static EntityNotFoundException createEntityNotFound(Long id) {
            return EntityNotFoundException.createEntityNotFound(id);
        }

        public static EntityNotFoundException createEntityNotFoundByName(String name) {
            return EntityNotFoundException.createEntityNotFoundByName(name);
        }

        public static EntityNotFoundException createEntityNotFoundByField(String fieldName, Object value) {
            return EntityNotFoundException.createEntityNotFoundByField(fieldName, value);
        }

        public static EntityNotFoundException createEntityNotFound(String entityName) {
            return EntityNotFoundException.createEntityNotFound(entityName);
        }

        public static EntityNotFoundException createEntityNotFoundByUserID(Long userId) {
            return EntityNotFoundException.createEntityNotFoundByUserID(userId);
        }

        public static EntityNotFoundException createSlugNotExist(String slug) {
            return EntityNotFoundException.createSlugNotExist(slug);
        }
        
    }
    
    public static class Access {
        
        public static class Role {
            
            public static AccessDeniedException createAdminRoleRequired(String msg) {
                return AccessDeniedException.createAdminRoleRequired(msg);
            }
            
        }
        
        public static class Jwt {
            
            public static AccessDeniedException createJwtAndJsonIdsNotEquals(Long ssoId, Long jsonCheckedFieldId) {
                return AccessDeniedException.createJwtAndJsonIdsNotEquals(ssoId, jsonCheckedFieldId);
            }

            public static AccessDeniedException createJwtAndEntityIdsNotEquals(Long ssoId, Long entityCheckedFieldId) {
                return AccessDeniedException.createJwtAndEntityIdsNotEquals(ssoId, entityCheckedFieldId);
            }
            
        }
        
        public static class Entity {
            
            public static AccessDeniedException createModifyNotOwnEntity(Long ssoId, Long entityCheckedFieldId) {
                return AccessDeniedException.createModifyNotOwnEntity(ssoId, entityCheckedFieldId);
            }

            public static AccessDeniedException createDeletingNotOwnEntities(Long ssoId) {
                return AccessDeniedException.createDeletingNotOwnEntities(ssoId);
            }
            
        }

        public static ForbiddenException createForbiddenException() {
            return ForbiddenException.createForbiddenException();
        }

        public static ProhibitedOperationException createProhibitedOperationException(String message) {
            return ProhibitedOperationException.createProhibitedOperationException(message);
        }

    }

    public static ExecutionException createExecutionException(String message) {
        return ExecutionException.createExecutionException(message);
    }

    public static SiblingForwardException createSiblingForwardException(String sibling, String message) {
        return SiblingForwardException.createSiblingForwardException(sibling, message);
    }
    
}
