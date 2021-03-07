package com.sertapp.controllers;

import com.sertapp.models.Persona;
import com.sertapp.controllers.util.JsfUtil;
import com.sertapp.controllers.util.JsfUtil.PersistAction;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "personaController")
@SessionScoped
public class PersonaController implements Serializable {

    @EJB
    private com.sertapp.controllers.PersonaFacade ejbFacade;
    private List<Persona> items = null;
    private Persona selected;
    UploadedFile file;
    public byte uploadResult[] = new byte[2048];

    public byte[] getUploadResult() {
        return uploadResult;
    }

    public void setUploadResult(byte[] uploadResult) {
        this.uploadResult = uploadResult;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {

        this.file = file;
    }

    public void report(ActionEvent event) throws JRException, IOException {
        
        Map<String, Object> parametros = new HashMap<String, Object>() ;
        parametros.put("madeBy", "Daniel Orozco");
        List<Persona> items = null;
        items = getFacade().findAll();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report.jasper"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros,
                new JRBeanCollectionDataSource(items));
        
        HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();
        
        response.addHeader("Content-disposition", "attachment; filename= Reporte.pdf");
        
        ServletOutputStream stream = response.getOutputStream();
        
        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
        
        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
        
    }

    public PersonaController() {
    }

    //Function for img upload
    public void upload() {
        try {

            String fileName = file.getFileName();
            String contentType = file.getContentType();
            InputStream input = file.getInputstream();
            uploadResult = IOUtils.toByteArray(input);
            FacesMessage mssg = new FacesMessage("Archivo ", file.getFileName() + "subido exitosamente!");
            FacesContext.getCurrentInstance().addMessage(null, mssg);

        } catch (Exception e) {
            FacesMessage mssg = new FacesMessage("Hubo un error...");
            FacesContext.getCurrentInstance().addMessage(null, mssg);
        }

    }

    public Persona getSelected() {
        return selected;
    }

    public void setSelected(Persona selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonaFacade getFacade() {
        return ejbFacade;
    }

    public Persona prepareCreate() {
        selected = new Persona();
        selected.setFoto(uploadResult);
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PersonaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PersonaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PersonaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Persona> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Persona> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Persona> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Persona.class)
    public static class PersonaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonaController controller = (PersonaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personaController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Persona) {
                Persona o = (Persona) object;
                return getStringKey(o.getDui());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Persona.class.getName()});
                return null;
            }
        }

    }

}
