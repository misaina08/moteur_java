/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import dao.BaseDao;
import java.util.List;
import modele.Prof;


/**
 *
 * @author Misaina
 */
public class Main {
    public static void main(String[] args)
    {
        BaseDao bd=null;
        try
        {
           bd=new BaseDao();
           
//           findall
           List<Object> res=bd.findAll(new Prof());
           for(Object o : res)
           {
               System.out.println(((Prof)o).getNom());
           }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
}
