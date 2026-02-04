package com.challege.literalura.servicio;

 import org.springframework.stereotype.Service;
 
 import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConvierteDatos {
   private ObjectMapper mapper = new ObjectMapper();

    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return mapper.readValue(json, clase);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
