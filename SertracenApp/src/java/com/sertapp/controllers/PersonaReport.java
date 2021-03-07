/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sertapp.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import com.sertapp.models.Persona;
import java.util.*;

/**
 *
 * @author Daniel Alejandro Orozco Orellana <00200617@uca.edu.sv>
 */
@ManagedBean
@RequestScoped
public class PersonaReport {
    
    private List<Persona> personas = new ArrayList<Persona>();

    public List<Persona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Persona> personas) {
        this.personas = personas;
    }
    
    
    
    
    
}
