package utilitaire;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Util {
	
	public Field[] getFields(Class obj)
	{
		Field[] res=null;

		String nomPackageSuper=obj.getSuperclass().getSimpleName();
		Field[] fieldObj=obj.getDeclaredFields();
		Field[] fieldSuper=obj.getSuperclass().getDeclaredFields();
		
		if(nomPackageSuper.compareTo("BaseModele")!=0)
		{
			res=new Field[fieldObj.length+fieldSuper.length];
			
			int indice=0;
			int i=0;
			for(i=0; i<fieldObj.length; i++)
			{
				res[i]=fieldObj[i];
			}
			indice=i;
			for(int a=0; a<fieldSuper.length; a++)
			{
				res[indice]=fieldSuper[a];				
				indice++;
			}
		}
		else
		{
			res=new Field[fieldObj.length];
			for(int i=0; i<fieldObj.length; i++)
			{
				res[i]=fieldObj[i];
			}
		}
		
		return res;
	}
	
//	date : entrée string => mm/jj/aaaa
//	vérification jj/mm/aaaa si valide, => mm/jj/aaaa
	public String checkDate(String date) throws IllegalArgumentException, Exception
	{
		String[] split=date.split("/");
		if(new Integer(split[0]).intValue()>31) throw new Exception("Jour invalide");
		if(new Integer(split[1]).intValue()>12) throw new Exception("Mois invalide");
		return split[1]+"/"+split[0]+"/"+split[2];
	}
	
//	date => jj/mm/yyyy
	public String dateToString(java.util.Date d)
	{
		String day=d.getDate()+"";
		String mois=(d.getMonth()+1)+"";
		if(d.getDate()<10) day="0"+d.getDate();
		if(d.getMonth()<9) mois="0"+(d.getMonth()+1);
		
		return day+"/"+mois+"/"+(d.getYear()+1900);
	}
	public int calculPage(int nbData,int nbParPage)
    {
        int reponse = 0;
        float inter = (float)nbData / (float)nbParPage;
        
        if (inter -((int)inter) > 0.0)
        {
            reponse = nbData / nbParPage+1;
        }
        else
        {
            reponse=nbData / nbParPage;
        }
        return reponse;
    }
	public String premierMaj(String s)
	{
		char[] lettreChar=s.toCharArray();
		String pl=String.valueOf(lettreChar[0]).toUpperCase();
		lettreChar[0]=pl.toCharArray()[0];
		return String.valueOf(lettreChar);
	}
	
//	générer valeur par défaut si value==null ou value vide
	public Object setDefaultValueObjectNull(Object objet, String value)
	{
		Object res=value;
		String typeObjet=objet.getClass().getName();
//		objet=value;
		if(value.toString().compareTo("")==0 || value.toString().compareTo("jj/mm/aaaa")==0)
		{
			
			if(typeObjet.compareTo("java.lang.String")==0)
			{
				res="-";
			}
			else if(typeObjet.compareTo("java.util.Date")==0 )
			{
				Date d=new Date();
				d.setMonth(1);
				d.setYear(1900);
				d.setDate(1);
				d.setMinutes(0);
				d.setHours(0);
				d.setSeconds(0);
				res="1/2/3800";
			}
			else if(typeObjet.compareTo("java.lang.Integer")==0) res=123456;
			else if(typeObjet.compareTo("java.lang.Double")==0) res=123456.0;
			else res=null;
		}
		return res;
		
	}
	
}
