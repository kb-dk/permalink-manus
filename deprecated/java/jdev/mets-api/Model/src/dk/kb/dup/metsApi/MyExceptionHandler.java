package dk.kb.dup.metsApi;

import oracle.toplink.exceptions.DatabaseException;
import oracle.toplink.exceptions.ExceptionHandler;

public class MyExceptionHandler implements ExceptionHandler {

  public Object handleException(RuntimeException exception) {
    if ((exception instanceof DatabaseException)){
      if( exception.getMessage().equals( "No more data to read from socket") || 
          exception.getMessage().equals( "OALL8 is in an inconsistent state.") || 
          exception.getMessage().equals( "Broken pipe")) {
            DatabaseException de = (DatabaseException)exception;
            de.getAccessor().reestablishConnection(de.getSession());
            return de.getSession().executeQuery(de.getQuery());
      }
    }
		throw exception;
  }
}