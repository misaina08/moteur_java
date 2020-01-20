package modele;

import java.lang.reflect.Method;

public class BaseModele {
	private String nomTable;
	private String prefixePk;
	private String nomPk;
	private String pk;
	public BaseModele()
	{
		
	}
	public BaseModele(String nt, String ppk, String npk) throws Exception
	{
		setNomTable(nt);
		setPrefixePk(ppk);
		setNomPk(npk);
	}
	public String getNomTable()
	{
		return nomTable;
	}
	public void setNomTable(String n) throws Exception
	{
		if(n=="") throw new Exception("Nom de table invalide");
		else nomTable=n;
	}
	public String getPrefixePk()
	{
		return prefixePk;
	}
	public void setPrefixePk(String pk)
	{
		prefixePk=pk;
	}
	public String getNomPk()
	{
		return nomPk;
	}
	public void setNomPk(String p)
	{
		nomPk=p;
	}
	public String getPk()
	{
		return pk;
	}
	public void setPk(String p)
	{
		pk=p;
	}
	public Object makeModele()
	{
		return null;
	}
	
//	verification d'une fonction si c'est une getter ou une setter
	public boolean isGetter(Method method){
		  if(!method.getName().startsWith("get"))      return false;
		  if(void.class.equals(method.getReturnType())) return false;
		  return true;
		}

		public boolean isSetter(Method method){
		  if(!method.getName().startsWith("set")) return false;
		  return true;
		}
	
}
