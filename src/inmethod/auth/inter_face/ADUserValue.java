package inmethod.auth.inter_face;
import java.util.*;

public class ADUserValue {
 private String account;
 private String name;
 private ArrayList<String> groups;
 private String email;
 private boolean enabled;

 public ADUserValue() {
   groups = new ArrayList<String>();
 }

 public void setAccountID(String account){
   this.account = account;
 }

 public String getAccountID(){
 return account;
 }

 public void setName(String sName){
   name = sName;
 }

 public String getName(){
   return name;
 }

 public void addGroups(String sGroups){
   groups.add(sGroups);
 }

 public ArrayList<String> getGroups(){
   return groups;
 }

 public void setAccoundEnable(boolean bStatus){
   enabled = bStatus;
 }
 public boolean isAccountEnable(){
   return enabled;
 }

 public void setEmail(String sEmail){
   email = sEmail;
 }
 public String getEmail(){
   return email;
 }
}