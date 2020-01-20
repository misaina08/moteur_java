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
public class Cours extends BaseModele{
    private String id;
    private String intitule;

    public Cours() throws Exception {
        super("Cours", "COURS", "ID");
    }

    public Cours(String id, String intitule, String nt, String ppk, String npk) throws Exception {
        super("Cours", "COURS", "ID");
        this.id = id;
        this.intitule = intitule;
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }
}
