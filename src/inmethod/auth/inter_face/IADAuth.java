package inmethod.auth.inter_face;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.*;
import java.io.*;

/**
 * 連到AD server,驗證密碼,然取得該帳號的屬性
 */
public class IADAuth
{
  private String domain;
  private String ldapHost;
  private String searchBase;

  public static void main(String a[]){

    IADAuth aAuth = new IADAuth("test.com","192.168.1.1","OU=Taipei,dc=test,dc=com");
    ADUserValue aUserInfo = aAuth.getUserInfo("administrator","xxxxx","920405");
    
  }

  private IADAuth(){}

  public IADAuth(String domain, String host,String searchBase){
    this(domain,host,"389",searchBase);
  }

  public IADAuth(String domain, String host,String port, String searchBase)
  {
    this.domain = domain;
    this.ldapHost = "ldap://"+host+":"+port;
    this.searchBase = searchBase;
  }



  /**
   * @return null if no data, or return all attributes (cn, sn,mail....)
   */
  public Map authenticate(String user, String pass)
  {
//    String returnedAtts[] ={ "sn", "givenName", "mail" };
    String searchFilter = "(&(objectClass=user)(sAMAccountName=" + user + "))";

    //Create the search controls
    SearchControls searchCtls = new SearchControls();
  //  searchCtls.setReturningAttributes(returnedAtts);

    //Specify the search scope
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    Hashtable env = new Hashtable();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, ldapHost);
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain);
    env.put(Context.SECURITY_CREDENTIALS, pass);

    LdapContext ctxGC = null;

    try
    {
      ctxGC = new InitialLdapContext(env, null);
      //Search objects in GC using filters
      NamingEnumeration answer = ctxGC.search(searchBase, searchFilter, searchCtls);
      while (answer.hasMoreElements())
      {
        SearchResult sr = (SearchResult) answer.next();
        Attributes attrs = sr.getAttributes();
        Map amap = null;
        if (attrs != null)
        {
          amap = new HashMap();
          NamingEnumeration ne = attrs.getAll();
          while (ne.hasMore())
          {
            Attribute attr = (Attribute) ne.next();
            //System.out.println("ID="+attr.getID()+"="+attr.get());
            amap.put(attr.getID(), attr.get());
          }
          ne.close();
        }
          return amap;
      }
    }
    catch (NamingException ex)
    {
      ex.printStackTrace();
    }

    return null;
  }

  public ADUserValue getUserInfo(String sLdapAccountID, String sLdapPassword,String sSearchAccountID)
  {
//    String returnedAtts[] ={ "sn", "givenName", "mail" };
    ADUserValue aUserInfo = new ADUserValue();
    aUserInfo.setAccountID(sSearchAccountID);
    String searchFilter = "(&(objectClass=user)(sAMAccountName=" + sSearchAccountID + "))";

    //Create the search controls
    SearchControls searchCtls = new SearchControls();
  //  searchCtls.setReturningAttributes(returnedAtts);

    //Specify the search scope
    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    Hashtable env = new Hashtable();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, ldapHost);
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, sLdapAccountID + "@" + domain);
    env.put(Context.SECURITY_CREDENTIALS, sLdapPassword);

    LdapContext ctxGC = null;

    try
    {
      ctxGC = new InitialLdapContext(env, null);
      //Search objects in GC using filters
      NamingEnumeration answer = ctxGC.search(searchBase, searchFilter, searchCtls);
      if(answer.hasMoreElements())
      {
        SearchResult sr = (SearchResult) answer.next();
        Attributes attrs = sr.getAttributes();
          Attribute groupsAttribute = attrs.get("memberOf");
          Attribute bitsAttribute = attrs.get("userAccountControl");
          Attribute mailAttribute = attrs.get("mail");
          Attribute nameAttribute = attrs.get("name");
          Attribute accountAttribute = attrs.get("sAMAccountName");

          aUserInfo.setAccountID((String) accountAttribute.get(0).toString() );

          if (groupsAttribute != null) {
            for (int i = 0; i < groupsAttribute.size(); i++) {
              aUserInfo.addGroups((String) groupsAttribute.get(i));
            }
          }
          if (bitsAttribute != null) {
            long lng = Long.parseLong(bitsAttribute.get(0).toString());
            long secondBit = lng & 2; // get bit 2
            if (secondBit == 0) {
              aUserInfo.setAccoundEnable(true);
            }
          }
          if (mailAttribute != null) {
            aUserInfo.setEmail((String) mailAttribute.get(0).toString());
          }
          if (nameAttribute != null) {
            aUserInfo.setName( (String) nameAttribute.get(0).toString() );
          }


     //   System.out.println("----\nUser: " + aUserInfo.getName());
     //   System.out.println("Enabled: " + aUserInfo.isAccountEnable() );
     //   System.out.println("Email: " + aUserInfo.getEmail() );
     //   System.out.println("Account: " + aUserInfo.getAccountID() );
        /*
        for (String groupName:aUserInfo.getGroups()){
          System.out.println(aUserInfo.getName() + " is a member of: " + groupName);
        }*/
        return aUserInfo;
      }
    }
    catch (NamingException ex)
    {
      ex.printStackTrace();
    }

    return null;
  }

  
  public   ArrayList<ADUserValue> getAllUsers(String user, String password) {
	  ArrayList<ADUserValue> aReturn = new  ArrayList<ADUserValue>();
	    try {

	      Properties p = new Properties();

	      //create an initial directory context
	      Hashtable env = new Hashtable();
	      env.put(Context.INITIAL_CONTEXT_FACTORY,
	          "com.sun.jndi.ldap.LdapCtxFactory");
	      env.put(Context.PROVIDER_URL,  ldapHost);
          env.put(Context.SECURITY_AUTHENTICATION, "simple");
	      env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain);
	      env.put(Context.SECURITY_CREDENTIALS, password);
	      env.put(Context.REFERRAL, "follow");
	      // Create the initial directory context
	      DirContext ctx = new InitialDirContext(env);

	      //get all the users list and their group memberships
	      //System.out.println(ctx+searchBase);
	      ArrayList<ADUserValue> aAllUsers = listSubContext(ctx, searchBase);
	      /*
	      int i=0;
	      for(ADUserValue record:aAllUsers){
	    	  i++;
	      	System.out.println(i+ "name = " + record.getName() );  
	      }*/
	      return aAllUsers;
	    } catch (NamingException e) {
	      e.printStackTrace();
	      return aReturn;
	    }
	  }

	  private ArrayList<ADUserValue> listSubContext(DirContext ctx, String nm) throws NamingException {
	    String[] attributeNames = { "memberOf", "userAccountControl", "mail",
	        "name", "sAMAccountName" };
	    NamingEnumeration contentsEnum = ctx.list(nm);
        ArrayList<ADUserValue> aReturn =  new ArrayList<ADUserValue>();
	    while (contentsEnum.hasMore()) {
	    
	      NameClassPair ncp = (NameClassPair) contentsEnum.next();
	      String userName = ncp.getName();
	      
	      Attributes attr1 =null;
	      try{
	    	  if( userName.indexOf("Configuration")!=-1 ) continue;
	    	  if( userName.indexOf("DomainDnsZones")!=-1 ) continue;
	    	  if( userName.indexOf("System")!=-1 ) continue;
	    	  if( userName.indexOf("Policies")!=-1 ) continue;
	    	  if( userName.indexOf("Computers")!=-1 ) continue;
	    	  if( userName.indexOf("aliases")!=-1 ) continue;
	    	  if( userName.indexOf("Builtin")!=-1 ) continue;
	    	  if( userName.indexOf("ForestDnsZones")!=-1 ) continue;
	    	  
	    	  
	        attr1=  ctx.getAttributes(userName + "," + nm,
	        new String[] { "objectcategory" });
	      }catch(Exception ee){
	    	  continue;
	      }
	      if ( attr1 == null || attr1.get("objectcategory")==null ) return aReturn;
	      if (attr1.get("objectcategory").toString().indexOf("CN=Person") == -1) {
	        // Recurse sub-contexts
    	    //System.out.println(ctx+userName+nm);
	    	aReturn.addAll(listSubContext(ctx, userName + "," + nm) );
	      } else {
	    	  
	    	
	    	ADUserValue rec = new ADUserValue();
	        try {
	          Attributes attrs = ctx.getAttributes(userName + "," + nm,
	              attributeNames);
	          Attribute groupsAttribute = attrs.get("memberOf");
	          Attribute bitsAttribute = attrs.get("userAccountControl");
	          Attribute mailAttribute = attrs.get("mail");
	          Attribute nameAttribute = attrs.get("name");
	          Attribute accountAttribute = attrs.get("sAMAccountName");

	          if (accountAttribute != null) {
	            for (int i = 0; i < accountAttribute.size(); i++) {
	              rec.setAccountID( (String) accountAttribute.get(i) );
	            }
	          }
	          if (groupsAttribute != null) {
	            for (int i = 0; i < groupsAttribute.size(); i++) {
	              rec.addGroups((String) groupsAttribute.get(i));
	            }
	          }
	          if (bitsAttribute != null) {
	            long lng = Long.parseLong(bitsAttribute.get(0).toString());
	            long secondBit = lng & 2; // get bit 2
	            if (secondBit == 0) {
	              rec.setAccoundEnable( true );
	            }
	          }
	          if (mailAttribute != null) {
	            for (int i = 0; i < mailAttribute.size(); i++) {
	              rec.setEmail( (String) mailAttribute.get(i) );
	            }
	          }
	          if (nameAttribute != null) {
	            for (int i = 0; i < nameAttribute.size(); i++) {
	              rec.setName( (String) nameAttribute.get(i) );
	            }
	          }
              aReturn.add(rec);
	        } catch (NamingException ne) {
	          ne.printStackTrace();
	        }
/*
	        for (Iterator iterator = rec.getGroups().iterator(); iterator.hasNext();) {
	          String groupName = (String) iterator.next();
	     //     System.out.println(rec.getName()  + " is a member of: " + groupName);
	        }
	*/        
	      }
	    }
	    return aReturn;
	  }
}