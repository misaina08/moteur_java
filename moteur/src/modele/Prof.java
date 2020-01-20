/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

/**
 *
 * @author Misaina
 */
public class Prof extends BaseModele{
    private String idEmp;
    private String nom;
    private float sal;
    private boolean conge;

    public Prof() throws Exception{
        super("Prof", "PROF", "IDEMP");
    }

    public Prof(String idEmp, String nom, float sal, boolean conge) throws Exception{
        super("Prof", "PROF", "IDEMP");
        this.idEmp = idEmp;
        this.nom = nom;
        this.sal = sal;
        this.conge = conge;
    }

   
    
    public String getIdEmp() {
        return idEmp;
    }

    public void setIdEmp(String idEmp) {
        this.idEmp = idEmp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getSal() {
        return sal;
    }

    public void setSal(float sal) {
        this.sal = sal;
    }

    public boolean isConge() {
        return conge;
    }

    public void setConge(boolean conge) {
        this.conge = conge;
    }

    
}
