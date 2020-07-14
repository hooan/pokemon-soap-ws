package io.cliber.pokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class PokemonService {
    static String uri = "https://pokeapi.co/api/v2/pokemon/";
    public Map getPokemon(String name){
        Map<String, Object> map = null;
        try {
            HttpEntity<String> entity = new HttpEntity<>("body", getHeader());
            ResponseEntity<String> response = getRest().exchange(uri+name, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
             map = mapper.readValue(response.getBody(), Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public List getEncounters(String name){
        Map<String, Object> map = null;
        List ppl2 = null;
        try {
            HttpEntity<String> entity = new HttpEntity<>("body", getHeader());
            ResponseEntity<String> response = getRest().exchange(uri+name, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(response.getBody(), Map.class);
            Integer id = (Integer) map.get("id");
            String nUri = "https://pokeapi.co/api/v2/pokemon/"+ id +"/encounters";
            response = getRest().exchange(nUri, HttpMethod.GET, entity, String.class);
            ppl2 = Arrays.asList(mapper.readValue(response.getBody(), Map[].class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ppl2;
    }
    private HttpHeaders getHeader(){
        HttpHeaders header = new HttpHeaders();
        header.add("Accept-Language","es-MX,es;q=0.8,en-US;q=0.5,en;q=0.3");
        header.add("Connection","keep-alive");
        header.add("Host","pokeapi.co");
        return header;

    }
    private RestTemplate getRest(){
            CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            return new RestTemplate(requestFactory);
        }
}
