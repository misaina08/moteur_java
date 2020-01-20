/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.sql.Time;
import java.util.Date;

/**
 *
 * @author Misaina
 */
public class CoursProf extends BaseModele{
    private String idCours;
    private String idEmp;
    private Date dateCours;
    private Time heureDebut;
    private Time heureFin;

    public CoursProf()throws Exception {
        super("CoursProf", "", "");
    }

    

    public CoursProf(String idCours, String idEmp, Date dateCours, Time heureDebut, Time heureFin, String nt, String ppk, String npk) throws Exception {
        super("CoursProf", "", "");
        this.idCours = idCours;
        this.idEmp = idEmp;
        this.dateCours = dateCours;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public String getIdCours() {
        return idCours;
    }

    public void setIdCours(String idCours) {
        this.idCours = idCours;
    }

    public String getIdEmp() {
        return idEmp;
    }

    public void setIdEmp(String idEmp) {
        this.idEmp = idEmp;
    }

    public Date getDateCours() {
        return dateCours;
    }

    public void setDateCours(Date dateCours) {
        this.dateCours = dateCours;
    }

    public Time getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(Time heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Time getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(Time heureFin) {
        this.heureFin = heureFin;
    }
    
}
