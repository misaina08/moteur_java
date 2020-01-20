package dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import modele.BaseModele;
import utilitaire.Connexion;
import utilitaire.Util;

public class BaseDao {
	public String requete;
	public int nbParPage=20;
	public BaseDao()
	{
		
	}
	
	private String buildQueryUpdate(BaseModele bm) throws Exception
	{

		String colonnes="";
		String critere=" where "+bm.getNomPk()+"=?";
		
		Field[] champs=bm.getClass().getDeclaredFields();
//		le compteur commence par 1 car on ne prend pas le pk
//		for(int i=1; i<champs.length; i++)
//		{
//			colonnes=colonnes+champs[i].getName().toString()+"=?";
//			if(i<champs.length-1) 
//			{
//				colonnes=colonnes+", ";
//			}
//		}
		String virgule="";
		String condition="";
		Util util=new Util();
		
		int notNullCourant=0;
		int notNullNext=0;
		for(int i=1; i<champs.length;i++)
		{
			Method met=bm.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
			Object obj=met.invoke(bm, null);
			if(obj!=null)
			{
				if(obj.getClass().getName().compareTo("java.lang.String")==0)
				{
					if(obj.toString().compareTo("-")!=0)
					{
						virgule=" , ";
						condition=condition+ champs[i].getName()+"='"+obj.toString()+"'";
						notNullCourant++;
					}
				}
				else if(obj.getClass().getName().compareTo("java.util.Date")==0)
				{
					if(obj.toString().compareTo("Sat Feb 01 00:00:00 GMT+01:00 3800")!=0)
					{
						virgule=" , ";
						// dat=String.valueOf(((Date)obj).getDate())+"/"+String.valueOf(((Date)obj).getMonth()+1)+"/"+String.valueOf(((Date)obj).getYear()+1900);
						String dat=util.dateToString((Date)obj);
						condition=condition+champs[i].getName()+"='"+dat+"'";
						notNullCourant++;
					}
				}
				else if(obj.getClass().getName().compareTo("java.lang.Integer")==0)
				{
					if(((Integer)obj).compareTo(new Integer(123456))!=0)
					{
						virgule=" , ";
						condition=condition+ champs[i].getName()+"="+((Integer)obj).intValue();
						notNullCourant++;
					}
				}
				else if(obj.getClass().getName().compareTo("java.lang.Double")==0)
				{
					if(((Double)obj).compareTo(new Double(123456))!=0)
					{
						virgule=" , ";
						condition=condition+ champs[i].getName()+"="+((Double)obj).doubleValue();
						notNullCourant++;
					}
				}
				else
				{
					Method metFk=bm.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					Object bmForeignKey=metFk.invoke(bm, null);
					
					if(bmForeignKey!=null)
					{
						Method metGetIdFk=((BaseModele)bmForeignKey).getClass().getMethod("getId", null);
						Object idFk=metGetIdFk.invoke((BaseModele)bmForeignKey, null);
						
						if(idFk.toString().compareTo("-")!=0)
						{
							virgule=" , ";
							condition=condition+ champs[i].getName()+"='"+idFk.toString()+"'";
							notNullCourant++;
						}
					}
					
				}
				
				if(i<champs.length-1)
				{
					Method met1=bm.getClass().getMethod("get"+util.premierMaj(champs[i+1].getName()), null);
					Object obj1=met1.invoke(bm, null);
					if(obj1!=null)
					{
						if(obj1.getClass().getSimpleName().compareTo("String")!=0 &&
								obj1.getClass().getSimpleName().compareTo("Date")!=0 &&
								obj1.getClass().getSimpleName().compareTo("Integer")!=0 &&
								obj1.getClass().getSimpleName().compareTo("Double")!=0)
						{
							Method metGetIdFkPlus1=((BaseModele)obj1).getClass().getMethod("getId", null);
							Object idFkPlus1=metGetIdFkPlus1.invoke((BaseModele)obj1, null);
							
							if(idFkPlus1.toString().compareTo("-")!=0)
							{
								
								notNullNext++;
								if(notNullNext!=0 && notNullCourant!=0)
								{
									condition=condition+" , ";
								}
							}
//							
							
						}
						else
						{
							if((obj1.toString()).compareTo("-")!=0 && (obj1.toString()).compareTo("Sat Feb 01 00:00:00 GMT+01:00 3800")!=0 && (obj1.toString()).compareTo("123456")!=0 &&(obj1.toString()).compareTo("123456.0")!=0 && (obj1.toString()).compareTo("null")!=0 )
							{
								
								notNullNext++;
								if(notNullNext!=0 && notNullCourant!=0)
								{
									condition=condition+" , ";
								}
							}
						}
					}
				}
			}
			
		}
		String resultat="update "+bm.getNomTable()+" set "+condition+critere;
		System.out.println(resultat);
		return resultat;
	}
	
        public void update(BaseModele bm, Connection conn) throws Exception
        {
            
            PreparedStatement pst=null;
            try
            {
                    String r=buildQueryUpdate(bm);
                    pst=conn.prepareStatement(r);
                    Method m=bm.getClass().getMethod("getId", null);
                    String val=(String)m.invoke(bm, null);
                    pst.setString(1, val);
                    pst.executeUpdate();
            }
            catch(Exception e)
            {
                    e.printStackTrace();
                    throw e;
            }
            finally
            {
                    if(pst!=null) pst.close();
            }
        }
        
	public void update(BaseModele bm) throws Exception
	{
		Connexion c=new Connexion();
		Connection conn=null;
		
		try
		{
			Util util=new Util();
			String requete=buildQueryUpdate(bm);
			conn=c.getConn();
			update(bm, conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(conn!=null) conn.close();
		}
	}
	
	public void save(BaseModele baseModele, Connection con) throws Exception
	{
		Connexion c=new Connexion();
		Connection conn=null;
		PreparedStatement pst=null;
		
		Util util=new Util();

		try
		{

			String requete=buildQueryInsert(baseModele);
//			conn=c.getConn();
			pst=con.prepareStatement(requete);
			String idVrai=buildId(baseModele, con);
			
			Field[] champs=baseModele.getClass().getDeclaredFields();
			pst.setString(1, idVrai);
			System.out.println("id vrai----------------------"+idVrai);
			for(int i=1; i<champs.length;i++)
			{
				if(champs[i].getType().getName().compareToIgnoreCase("java.lang.Integer")==0)
				{
					Method m=baseModele.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					int val=((Integer)m.invoke(baseModele, null)).intValue();
					pst.setInt(i+1, val);
				}
				else if(champs[i].getType().getName().compareToIgnoreCase("java.lang.String")==0)
				{
					
					Method me=baseModele.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					String vale=(String)me.invoke(baseModele, null);
					pst.setString(i+1, vale);
				}
				else if(champs[i].getType().getName().compareToIgnoreCase("java.util.Date")==0)
				{
					
					Method me=baseModele.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					Date vale=(Date)me.invoke(baseModele, null);
					System.out.println("------------------------------"+vale.getMonth());
					pst.setDate(i+1, new java.sql.Date(vale.getYear(), vale.getMonth(), vale.getDate()));
				}
				else if(champs[i].getType().getName().compareToIgnoreCase("java.lang.Double")==0)
				{
					Method m=baseModele.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					double val=((Double)m.invoke(baseModele, null)).doubleValue();
					pst.setDouble(i+1, val);
				}
				else
				{
					Method m1=baseModele.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					Object fk=m1.invoke(baseModele, null);
					Method m2=fk.getClass().getMethod("getId", null);
					String idFk=(String)m2.invoke(fk, null);
					pst.setString(i+1, idFk);
				}
			}
			pst.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(pst!=null) pst.close();
//			if(conn!=null) conn.close();	
		}
	}
        public void save(BaseModele baseModele) throws Exception
	{
		Connexion c=new Connexion();
		Connection conn=null;
		try
		{

			String requete=buildQueryInsert(baseModele);
			conn=c.getConn();
                        save(baseModele, conn);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(conn!=null) conn.close();
		}
	}
	private String buildQueryInsert(BaseModele bm) 
	{
		String champs="";
		String valeurs="";
			
		Field[] fields=bm.getClass().getDeclaredFields();
//		construction de champs
		for(int i=0; i<fields.length; i++)
		{
			champs=champs+fields[i].getName().toString();
			valeurs=valeurs+"?";
			if(i<fields.length-1) 
			{
				champs=champs+", ";
				valeurs=valeurs+", ";
			}
		}		
		String requete="insert into "+bm.getNomTable()+" ("+champs+") values ("+valeurs+")";
		System.out.println("------------------------------"+requete);
		return requete;
	}
	public String buildId(BaseModele bm, Connection con) throws Exception
	{
		String id=bm.getPrefixePk();
		int nextSeq=0;
		try
		{
			nextSeq=getSequence("nextval", bm.getNomTable(), con);
			id=id+String.valueOf(nextSeq);
			return id;
		}
		catch(Exception e)
		{
			throw new Exception("Erreur dans buildId");
		}
		
	}
	
//	valeur : currval ou nextval
	public int getSequence(String valeur, String nomTable, Connection con) throws Exception
	{
		Connexion c=new Connexion();
		Connection conn=null;
		Statement pst=null;
		ResultSet rs=null;
		int resultat=0;
		try
		{
//			conn=c.getConn();
			pst=con.createStatement();
			rs=pst.executeQuery("select seq_"+nomTable+"."+valeur+" as val from dual");
			while(rs.next()){
				resultat=rs.getInt("val");
			}
			return resultat;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(rs!=null) rs.close();
			if(pst!=null) pst.close();
//			if(conn!=null) conn.close();
		}
		
	}
	
	public Object findById(BaseModele baseModele, Connection con) throws Exception
	{
		PreparedStatement pst=null;
		ResultSet rs=null;
		Class resultat=null;
		Object ob=null;
		try
		{
			resultat=Class.forName("modele."+baseModele.getNomTable());
//			conn=c.getConn();
			String requete="select * from "+baseModele.getNomTable()+" where "+baseModele.getNomPk()+"=?";
			
			pst=con.prepareStatement(requete); // ou conn

			pst.setString(1, baseModele.getPk());
			rs=pst.executeQuery();
//			obtenir les champs de l'objet
//			Field[] listeChamp=resultat.getDeclaredFields();
			Util util=new Util();
			Field[] listeChamp=util.getFields(resultat);
			ob=resultat.newInstance();
			while(rs.next())
			{
				for(int i=0;i<listeChamp.length;i++)
				{
					if(listeChamp[i].getType().getName().compareToIgnoreCase("java.lang.String")==0)
					{
						Class[] param=new Class[1];
						param[0]=String.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=rs.getString(listeChamp[i].getName());
						m.invoke(ob, o);
					}
					else if(listeChamp[i].getType().getName().compareToIgnoreCase("java.lang.Integer")==0)
					{
						Class[] param=new Class[1];
						param[0]=Integer.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=new Integer(rs.getInt(listeChamp[i].getName()));
						m.invoke(ob, o);
					}
					else if(listeChamp[i].getType().getName().compareToIgnoreCase("java.lang.Double")==0)
					{
						Class[] param=new Class[1];
						param[0]=Double.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=rs.getDouble(listeChamp[i].getName());
						m.invoke(ob, o);
					}
					else if(listeChamp[i].getType().getName().compareToIgnoreCase("java.util.Date")==0)
					{
						Class[] param=new Class[1];
						param[0]=java.util.Date.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						
						o[0]=new Date(rs.getDate(listeChamp[i].getName()).getTime());
						m.invoke(ob, o);
					}
					else
					{
//						System.out.println("___"+listeChamp[i].getType().getSimpleName());
						BaseModele bm1=new BaseModele(listeChamp[i].getType().getSimpleName(), "pref", "ID");
						bm1.setPk(rs.getString(listeChamp[i].getName()));
						Object oRes=findById(bm1, con);
						
						Class[] param=new Class[1];
						param[0]=oRes.getClass();
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						
						Object[] o=new Object[1];
						o[0]=oRes;
						m.invoke(ob, o);
					}
				}
			}
			return ob;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(rs!=null) rs.close();
			if(pst!=null) pst.close();
//			if(con!=null) conn.close();
		}
		
	}
	public List<Object> findAll(BaseModele baseModele, Connection con) throws Exception
	{
		List<Object> resFin=null;
		Connexion c=new Connexion();
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Class resultat=null;
		Object ob=null;
		try
		{
			
			resultat=Class.forName("modele."+baseModele.getNomTable());
//			conn=c.getConn();
			
//			String requete="select * from "+baseModele.getNomTable();
			String requete="SELECT *  FROM "+baseModele.getNomTable();
//			System.out.println(requete);
			pst=con.prepareStatement(requete);
			rs=pst.executeQuery();
//			obtenir les champs de l'objet
//			Field[] listeChamp=((Class)resultat).getDeclaredFields();
			
			
			Util util=new Util();
			Field[] listeChamp=util.getFields(resultat);
			resFin=new ArrayList<Object>();
			while(rs.next())
			{
				ob=resultat.newInstance();
				for(int i=0;i<listeChamp.length;i++)
				{
					if(listeChamp[i].getType().getName().compareToIgnoreCase("java.lang.String")==0)
					{
						Class[] param=new Class[1];
						param[0]=String.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=rs.getString(listeChamp[i].getName());
						m.invoke(ob, o);
					}
					else if(listeChamp[i].getType().getName().compareToIgnoreCase("java.lang.Integer")==0)
					{
						Class[] param=new Class[1];
						param[0]=Integer.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=new Integer(rs.getInt(listeChamp[i].getName()));
						m.invoke(ob, o);
					}
					else if(listeChamp[i].getType().getName().compareToIgnoreCase("java.lang.Long")==0)
					{
						Class[] param=new Class[1];
						param[0]=Long.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=rs.getLong(listeChamp[i].getName());
						m.invoke(ob, o);
					}
					else if(listeChamp[i].getType().getName().compareToIgnoreCase("java.util.Date")==0)
					{
						Class[] param=new Class[1];
						param[0]=java.util.Date.class;
						Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
						Object[] o=new Object[1];
						o[0]=new Date(rs.getDate(listeChamp[i].getName()).getTime());
						m.invoke(ob, o);
					}
					else
					{
//						System.out.println("----------"+listeChamp[i].getName());
						
						if(rs.getString(listeChamp[i].getName())!=null )
						{
							BaseModele bm1=new BaseModele(util.premierMaj(listeChamp[i].getType().getSimpleName()), "pref", "ID");
							bm1.setPk(rs.getString(listeChamp[i].getName()));

							Object oRes=findById(bm1, con);
							
							Class[] param=new Class[1];
							param[0]=oRes.getClass();
							Method m=resultat.getMethod("set"+util.premierMaj(listeChamp[i].getName()), param);
							
							Object[] o=new Object[1];
							o[0]=oRes;
							m.invoke(ob, o);
						}
						
					}
					
					
				}
				resFin.add(ob);
			}
			return resFin;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(rs!=null) rs.close();
			if(pst!=null) pst.close();
//			if(conn!=null) conn.close();
		}
		
	}
	public List<Object> findAll(BaseModele baseModele) throws Exception
	{
		List<Object> resFin=null;
		Connexion c=new Connexion();
		Connection conn=null;
		
		try
		{
                    conn=c.getConn();
			resFin=findAll(baseModele, conn);
                        return resFin;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			
			if(conn!=null) conn.close();
		}
		
	}
	
//	recherches dans les vues
//	nomObjet : nom table ou vue
	public String buildQueryFindBy(BaseModele bm, String nomObjet, String order) throws NoSuchMethodException, SecurityException, Exception
	{
		
		String where="";
		String condition="";
		Util util=new Util();
		Field[] champs=util.getFields(bm.getClass());
		int notNullCourant=0;
		int notNullNext=0;
		for(int i=0; i<champs.length;i++)
		{
			Method met=bm.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
			Object obj=met.invoke(bm, null);
			if(obj!=null)
			{
				if(obj.getClass().getName().compareTo("java.lang.String")==0)
				{
					if(obj.toString().compareTo("-")!=0)
					{
						where=" where ";
						condition=condition+ "upper("+champs[i].getName()+")=upper('"+obj.toString()+"')";
						notNullCourant++;
					}
				}
				else if(obj.getClass().getName().compareTo("java.util.Date")==0)
				{
					if(obj.toString().compareTo("Sat Feb 01 00:00:00 GMT+01:00 3800")!=0)
					{
						where=" where ";
						// dat=String.valueOf(((Date)obj).getDate())+"/"+String.valueOf(((Date)obj).getMonth()+1)+"/"+String.valueOf(((Date)obj).getYear()+1900);
						String dat=util.dateToString((Date)obj);
						condition=condition+champs[i].getName()+"='"+dat+"'";
						notNullCourant++;
					}
				}
				else if(obj.getClass().getName().compareTo("java.lang.Integer")==0)
				{
					if(((Integer)obj).compareTo(new Integer(123456))!=0)
					{
						where=" where ";
						condition=condition+ champs[i].getName()+"="+((Integer)obj).intValue();
						notNullCourant++;
					}
				}
				else if(obj.getClass().getName().compareTo("java.lang.Double")==0)
				{
					if(((Double)obj).compareTo(new Double(123456))!=0)
					{
						where=" where ";
						condition=condition+ champs[i].getName()+"="+((Double)obj).doubleValue();
						notNullCourant++;
					}
				}
				else
				{
					Method metFk=bm.getClass().getMethod("get"+util.premierMaj(champs[i].getName()), null);
					Object bmForeignKey=metFk.invoke(bm, null);
					
					if(bmForeignKey!=null)
					{
						Method metGetIdFk=((BaseModele)bmForeignKey).getClass().getMethod("getId", null);
						Object idFk=metGetIdFk.invoke((BaseModele)bmForeignKey, null);
						
						if(idFk.toString().compareTo("-")!=0)
						{
							where=" where ";
							condition=condition+ champs[i].getName()+"=upper('"+idFk.toString()+"')";
							notNullCourant++;
						}
					}
					
				}
				
				if(i<champs.length-1)
				{
					Method met1=bm.getClass().getMethod("get"+util.premierMaj(champs[i+1].getName()), null);
					Object obj1=met1.invoke(bm, null);
					if(obj1!=null)
					{
						if(obj1.getClass().getSimpleName().compareTo("String")!=0 &&
								obj1.getClass().getSimpleName().compareTo("Date")!=0 &&
								obj1.getClass().getSimpleName().compareTo("Integer")!=0 &&
								obj1.getClass().getSimpleName().compareTo("Double")!=0)
						{
							Method metGetIdFkPlus1=((BaseModele)obj1).getClass().getMethod("getId", null);
							Object idFkPlus1=metGetIdFkPlus1.invoke((BaseModele)obj1, null);
							
							if(idFkPlus1.toString().compareTo("-")!=0)
							{
								
								notNullNext++;
								if(notNullNext!=0 && notNullCourant!=0)
								{
									condition=condition+" and ";
								}
							}
//							
							
						}
						else
						{
							if((obj1.toString()).compareTo("-")!=0 && (obj1.toString()).compareTo("Sat Feb 01 00:00:00 GMT+01:00 3800")!=0 && (obj1.toString()).compareTo("123456")!=0 &&(obj1.toString()).compareTo("123456.0")!=0 && (obj1.toString()).compareTo("null")!=0 )
							{
								
								notNullNext++;
								if(notNullNext!=0 && notNullCourant!=0)
								{
									condition=condition+" and ";
								}
							}
						}
					}
				}
			}
			
		}
		String res="select * from "+bm.getNomTable()+where+condition+order;
		this.requete=res;
		System.out.println("requete recherche = "+requete);
		return res;
	}
	
//	etat : nom table ou vue
	public List<Object> findPagin(BaseModele baseModele, String etat, int numPage, String order, Connection con) throws Exception
	{
		List<Object> resFin=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		int rowNumSup=numPage*nbParPage;
		int rowNumInf=rowNumSup-nbParPage;
		try
		{
			String sousRequete=buildQueryFindBy(baseModele, etat, order);
			
			String requete="SELECT * FROM (SELECT  rownum rnum, sous.*  FROM ("+
					sousRequete+") sous WHERE rownum <= "+rowNumSup+" ) WHERE rnum > "+rowNumInf;
			System.out.println(requete);
			
			pst=con.prepareStatement(requete);
			rs=pst.executeQuery();
			Util util=new Util();
			
			resFin=new ArrayList<Object>();
			Class[] typeParam=new Class[1];
			typeParam[0]=String.class;
			Method metSetId=baseModele.getClass().getMethod("set"+util.premierMaj(new String(baseModele.getNomPk()).toLowerCase() ), typeParam);
			Method metSetPk=baseModele.getClass().getMethod("setPk", typeParam);
			Object resTemp=null;
			while(rs.next())
			{
//				alaina le id azo avy @le requete
				String id=rs.getString(baseModele.getNomPk());
				
				Object[] param=new Object[1];
				param[0]=id;
				metSetId.invoke(baseModele, param);
				metSetPk.invoke(baseModele, param);
				resTemp=findById(baseModele, con);
				
				resFin.add(resTemp);
			}
			
			return resFin;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(rs!=null) rs.close();
			if(pst!=null) pst.close();
		}
		
	}
	
        public List<Object> findPagin(BaseModele baseModele, String etat, int numPage, String order) throws Exception
	{
		List<Object> resFin=null;
		Connexion c=new Connexion();
                Connection conn=null;
		try
		{
			conn=c.getConn();
                        resFin=findPagin(baseModele, etat, numPage, order, conn);
			
			return resFin;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(conn!=null) conn.close();
		}
		
	}
        
	public List<Object> find(BaseModele baseModele, String etat, String order, Connection con) throws Exception
	{
		List<Object> resFin=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
	
		try
		{
			String r1=buildQueryFindBy(baseModele, etat, order);
			
			System.out.println(r1);
			
			pst=con.prepareStatement(requete);
			rs=pst.executeQuery();
			Util util=new Util();
			
			resFin=new ArrayList<Object>();
			Class[] typeParam=new Class[1];
			typeParam[0]=Integer.class;
			Method metSetId=baseModele.getClass().getMethod("set"+util.premierMaj(new String(baseModele.getNomPk()).toLowerCase() ), typeParam);
			Method metSetPk=baseModele.getClass().getMethod("setPk", typeParam);
			Object resTemp=null;
			while(rs.next())
			{
//				alaina le id azo avy @le requete
				String id=rs.getString(baseModele.getNomPk());
				
				Object[] param=new Object[1];
				param[0]=id;
				metSetId.invoke(baseModele, param);
				metSetPk.invoke(baseModele, param);
				resTemp=findById(baseModele, con);
				
				resFin.add(resTemp);
			}
			
			return resFin;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(rs!=null) rs.close();
			if(pst!=null) pst.close();
		}
		
	}
        public List<Object> find(BaseModele baseModele, String etat, String order) throws Exception
	{
		List<Object> resFin=null;
		Connexion c=new Connexion();
                Connection conn=null;
	
		try
		{
			conn=c.getConn();
                        resFin=find(baseModele, etat, order, conn);
			return resFin;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(conn!=null) conn.close();
			
		}
		
	}
	public void delete(BaseModele baseModele) throws Exception
	{
		Connexion c=new Connexion();
		Connection conn=null;
		try
		{
			conn=c.getConn();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			if(conn!=null) conn.close();	
		}
	}
	
        public void delete(BaseModele baseModele, Connection conn) throws Exception
	{
		
		try
		{
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
	}
        
//	get nombre de page des données filtrées dans la requete
    public int getNbPage(String requete, int nbParPage, Connection con)throws Exception
    {
    	int reponse=0;
		Statement stat=null;
		try{
            
            String query="select count(*) as numLigne from ("+requete+")";
            stat=con.createStatement();
            
            ResultSet rs=stat.executeQuery(query);
            Util util=new Util();
            while(rs.next()){
            	reponse=util.calculPage(rs.getInt("numLigne"), nbParPage);
            }
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		finally{
			if(stat!=null)stat.close();
		}
		return reponse;
    }
}
