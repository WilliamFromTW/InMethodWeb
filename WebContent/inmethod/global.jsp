<%@ page import="javax.servlet.http.*,javax.mail.*,java.util.*,inmethod_custom.Global,inmethod.auth.*,inmethod.auth.inter_face.*,inmethod.commons.util.*"%>
<%!public String DateToStr(java.util.Date dateArg) throws Exception{
    return (StringConverter.DateToStr(dateArg)).substring(0,10) ;
  }

  public String addSlashes(String nl_string){
    try{
      String s = StringConverter.replace(nl_string,"\"","\\\"");
      return s.replace("/","\\/");
    }catch(Exception ex){return nl_string;}
  }
  
  public String space2HtmlTag(String nl_string){
    return HTMLConverter.space2HtmlTag(nl_string);
  }
  public String nl2br(String nl_string){
    return HTMLConverter.nl2br(nl_string);
  }
  /**
   * 取得本系統之Root URL
   * @param String Base URL like this "http://localhost/contextRoot/"
   */
 	public String getBaseHead(HttpServletRequest request, HttpServletResponse response) {
		String sBaseHead = null;
		String sGetServerName, sGetServerPath;
		sGetServerName = request.getHeader("Host");
		sGetServerPath = request.getContextPath();
		if (sGetServerPath.lastIndexOf("/") == (sGetServerPath.length() - 1))
			sBaseHead = "<base id=\"base_id\" href=\"http://" + sGetServerName + sGetServerPath + "\">";
		else
			sBaseHead = "<base id=\"base_id\" href=\"http://" + sGetServerName + sGetServerPath + "/\">";
		

       sBaseHead = sBaseHead + "\n" + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">";
       sBaseHead = sBaseHead + "\n" + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\" />";	
	   sBaseHead = sBaseHead + "\n" + "<link rel=\"stylesheet\" href=\"bootstrap5/css/bootstrap.min.css\">";
	   sBaseHead = sBaseHead + "\n" + "<link rel=\"stylesheet\" href=\"https://unpkg.com/bootstrap-table@1.19.1/dist/bootstrap-table.min.css\">";
	   sBaseHead = sBaseHead + "\n" + "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css\">";
	   sBaseHead = sBaseHead + "\n" + "<link rel=\"stylesheet\" href=\"jquery-ui/css/jquery-ui.min.css\">";
	   sBaseHead = sBaseHead + "\n" + "<link rel=\"stylesheet\" href=\"jquery-ui/css/jquery.multiselect.css\">";
       sBaseHead = sBaseHead + "\n" + "<title></title>";

			
		return sBaseHead;
	}

    public String getJavaScript(){
    	String sReturn ="";
    	sReturn = sReturn + "<script src=\"jquery3/jquery-3.6.0.min.js\"></script>";
    	sReturn = sReturn + "<script src=\"jquery-ui/js/jquery-ui.min.js\"></script>";
    	sReturn = sReturn + "<script src=\"jquery-ui/js/jquery.multiselect.js\"></script>";
    	sReturn = sReturn + "<script src=\"bootstrap5/js/bootstrap.min.js\"></script>";
    	sReturn = sReturn + "<script src=\"https://unpkg.com/bootstrap-table@1.19.1/dist/bootstrap-table.min.js\"></script>";
        sReturn = sReturn + "<script src=\"js/global.js\"></script>";
    	return sReturn;
    }
    
    public String getLoginUserID(HttpServletRequest request, HttpServletResponse response){
      WebAuthentication aWebaWebAuth = inmethod.auth.AuthFactory.getWebAuthentication(request, response);
      String sReturn = "";
      try{
    	  sReturn = aWebaWebAuth.getUserPrincipal();
      }  catch(Exception eee){
    	  eee.printStackTrace();
      }
     // System.out.println("login="+sReturn);
      return sReturn;
    } 

    public String getLoginUserName(HttpServletRequest request, HttpServletResponse response){
        WebAuthentication aWebaWebAuth = inmethod.auth.AuthFactory.getWebAuthentication(request, response);
        String sReturn = "";
        try{
      	  sReturn = aWebaWebAuth.getUserNameByUserID(aWebaWebAuth.getUserPrincipal());
        }  catch(Exception eee){
      	  eee.printStackTrace();
        }
      //  System.out.println("login="+sReturn);
        return sReturn;
      } 
    
    public boolean isLogin(HttpServletRequest request, HttpServletResponse response){
      WebAuthentication aWebaWebAuth = inmethod.auth.AuthFactory.getWebAuthentication(request, response);
      boolean aReturn=false;
      try{
    	aReturn =  aWebaWebAuth.isLogin(aWebaWebAuth.getUserPrincipal());
      }catch(Exception ee){
      }
      //System.out.println("login="+aReturn);
       return aReturn;	
    }
    public boolean hasPermission(HttpServletRequest request, HttpServletResponse response){
        WebAuthentication aWebaWebAuth = inmethod.auth.AuthFactory.getWebAuthentication(request, response);
		sFunctionName = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);		
        boolean aReturn=false;
        try{
      	  aReturn =  aWebaWebAuth.hasPermission(aWebaWebAuth.getUserRoles(aWebaWebAuth.getUserPrincipal()), sFunctionName);
        }catch(Exception ee){
        }
        
        return aReturn;	
    }
    
    /**
     * @return 1: {"NoPermission":"FAIL"} , 2:  [{"function_url"},... ] see inmethod/auth/RoleAuthorizedPermission.java 
     * @see RoleAuthorizedPermission
     */
    public String getJsonAuthorizedFunctionInfoList(HttpServletRequest request, HttpServletResponse response){
    	 WebAuthentication aWebAuth = inmethod.auth.AuthFactory.getWebAuthentication(request, response);
         String sJson = "[]";
         try{
        	 sJson = Global.getJsonAuthorizedFunctionInfoList(aWebAuth.getUserPrincipal());
        	 return sJson;
         }catch(Exception ee){
        	 ee.printStackTrace();
         }
         
          return "{\"NoPermission\":\"FAIL\"}";
         
    }
     
     // 以group排序
     public Vector<RoleAuthorizedPermission> sortFunctionInfoListByGroup(Vector<RoleAuthorizedPermission> aVector){
        Vector<RoleAuthorizedPermission> aReturnVector = new Vector<RoleAuthorizedPermission>();
        if( aVector.size()==0 ) return null;
        TreeMap<String,RoleAuthorizedPermission> aTreeMap = new TreeMap<String,RoleAuthorizedPermission>();
        for(int i=0;i<aVector.size();i++){
          aTreeMap.put( ((RoleAuthorizedPermission)aVector.get(i)).getFunctionGroup()+i, aVector.get(i) );
        }
        Iterator<String> aIT = aTreeMap.keySet().iterator();
        while(aIT.hasNext()){
          aReturnVector.add( aTreeMap.get( aIT.next()) );
        }
        return aReturnVector;
    }
     
     /**
      * @param aTo mail "to" whom
      * @param aCc mail "cc" to whom
      * @param sTopic email subject
      * @param sMessage email content
      */
     public void sendmail(String sFrom,Vector<String> aTo,Vector<String> aCc,String sTopic,String sMessage){
       try{
    	 String sEncode = inmethod_custom.Global.getInstance().getEnvirenment("ENCODE");
    	 // from
    	 
    	 // session
    	 Session session = MailTool.buildSession( (String) inmethod_custom.Global.getInstance().getEnvirenment("MAIL_SERVER_HOST" ));
    	 // html with images
    	 HtmlMultiPart aHMP= new HtmlMultiPart(sEncode);
    	 aHMP.setContent(sMessage);
    	 MailTool.setEncode(sEncode);
    	 MailTool.mailSend(session,aTo,aCc,null,sFrom,sTopic,aHMP.getMultipart() ,null,null);
      }catch(Exception ex){}
    }     
     
    String sLogin = "inmethod/index.jsp";
    String sNoPermission = "inmethod/auth/NoPermission.jsp";
    String sFunctionName = "";%>