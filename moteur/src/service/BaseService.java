/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.BaseDao;
import java.sql.Connection;
import utilitaire.Connexion;

/**
 *
 * @author Misaina
 */
public class BaseService {
    private Connection connexion;
    private BaseDao dao;

    public BaseDao getDao() {
        return dao;
    }

    public void setDao(BaseDao dao) {
        this.dao = dao;
    }
    public BaseService()
    {
        Connexion connex=new Connexion();
        setConnexion(connex.getConn());
        dao=new BaseDao();
    }
    
    public Connection getConnexion() {
        return connexion;
    }

    public void setConnexion(Connection connexion) {
        this.connexion = connexion;
    }
    
    
}
