package dk.kb.dup.metsApi;

import java.lang.Exception;

public class MyExceptionHandler  {


  public Object handleException(Exception exception) throws Exception {
    if ((exception instanceof Exception)){
      if( exception.getMessage().equals( "No more data to read from socket") || 
          exception.getMessage().equals( "OALL8 is in an inconsistent state.") || 
          exception.getMessage().equals( "Broken pipe")) {
	  return null;
      }
    }
    throw exception;
  }
}
