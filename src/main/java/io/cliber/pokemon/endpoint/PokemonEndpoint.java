package io.cliber.pokemon.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cliber.*;
import io.cliber.pokemon.model.Log;
import io.cliber.pokemon.service.LogRepository;
import io.cliber.pokemon.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Endpoint
    public class PokemonEndpoint {
        private static final String NAMESPACE_URI = "http://cliber.io";

        private PokemonService pokemonService;
        private ObjectMapper mapper = new ObjectMapper();

    private HttpServletRequest httpServletRequest;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.httpServletRequest = request;
    }

        @Autowired
        private LogRepository rep;

         @Autowired
         public void setApp(PokemonService pokemonService) {
            this.pokemonService = pokemonService;
        }
        @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAbilitiesRequest")
        @ResponsePayload
        public GetAbilitiesResponse getAbilities(@RequestPayload GetAbilitiesRequest request) throws IOException {
            GetAbilitiesResponse response = new GetAbilitiesResponse();
            Map map = pokemonService.getPokemon(request.getName());
            List json = (List) map.get("abilities");
            json.forEach( v -> {
                Map abiMap = (Map) v;
                Ability ab = new Ability();
                ab.setSlot((int) abiMap.get("slot"));
                ab.setIsHidden((boolean) abiMap.get("is_hidden"));
                Map abiMap2 = (Map) abiMap.get("ability");
                AbilityItem abi = new AbilityItem();
                abi.setName(String.valueOf(abiMap2.get("name")));
                abi.setUrl(String.valueOf(abiMap2.get("url")));
                ab.setAbilityItem(abi);
                response.getAbilities().add(ab);
            });
            rep.save(new Log(this.httpServletRequest.getRemoteAddr(),"getAbilities"));
            return response;
        }

        @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNameRequest")
        @ResponsePayload
        public GetNameResponse getName(@RequestPayload GetNameRequest request) {
            GetNameResponse response = new GetNameResponse();
            Map map = pokemonService.getPokemon(request.getName());
            response.setName(String.valueOf(map.get("name")));
            rep.save(new Log(this.httpServletRequest.getRemoteAddr(),"getName"));

            return response;
        }

      @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getIdRequest")
        @ResponsePayload
        public GetIdResponse getId(@RequestPayload GetIdRequest request) {
            GetIdResponse response = new GetIdResponse();
            Map map = pokemonService.getPokemon(request.getName());
            response.setId(new BigInteger(String.valueOf(map.get("id"))));
          rep.save(new Log(this.httpServletRequest.getRemoteAddr(),"getId"));

          return response;
        }

        @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBaseExperienceRequest")
        @ResponsePayload
        public GetBaseExperienceResponse getBaseExperience(@RequestPayload GetBaseExperienceRequest request) {
            GetBaseExperienceResponse response = new GetBaseExperienceResponse();
            Map map = pokemonService.getPokemon(request.getName());
            response.setBaseExperience(new BigInteger(String.valueOf(map.get("base_experience"))));
            rep.save(new Log(this.httpServletRequest.getRemoteAddr(),"getBaseExperience"));
            return response;
        }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHeldItemsRequest")
    @ResponsePayload
    public GetHeldItemsResponse getHeldItems(@RequestPayload GetHeldItemsRequest request) {
        GetHeldItemsResponse response = new GetHeldItemsResponse();
        Map map = pokemonService.getPokemon(request.getName());
        List json = (List) map.get("held_items");
        json.forEach( v -> {
            HeldItem item = new HeldItem();
            Map m1 = (Map) v;
            Map itemMap = (Map) m1.get("item");
            List versionList = (List) m1.get("version_details");
            HeldItemItem itemItem = new HeldItemItem();
                itemItem.setName(String.valueOf(itemMap.get("name")));
                itemItem.setUrl(String.valueOf(itemMap.get("url")));
            versionList.forEach( v1 -> {
                Map vrMap = (Map) v1;
                VersionDetail vd = new VersionDetail();
                    vd.setRarity(new BigInteger(String.valueOf(vrMap.get("rarity"))));
                    Map vrMap1 = (Map) vrMap.get("version");
                    Version vr = new Version();
                        vr.setName(String.valueOf(vrMap1.get("name")));
                        vr.setUrl(String.valueOf(vrMap1.get("url")));
                    vd.setVersion(vr);
                    item.setVersionDetails(vd);
            });

                item.setItem(itemItem);
            response.getHeldItems().add(item);
        });
        rep.save(new Log(this.httpServletRequest.getRemoteAddr(),"getHeldItems"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLocalAreaEncountersRequest")
    @ResponsePayload
    public GetLocalAreaEncountersResponse getLocalAreaEncounters(@RequestPayload GetLocalAreaEncountersRequest request) {
        GetLocalAreaEncountersResponse response = new GetLocalAreaEncountersResponse();
        List list = pokemonService.getEncounters(request.getName());
        list.forEach( v -> {
            Map m1 = (Map) v;
            Map locationMap = (Map) m1.get("location_area");
            List versionList = (List) m1.get("version_details");
            LocationArea lArea = new LocationArea();
                lArea.setName(String.valueOf(locationMap.get("name")));
                lArea.setUrl(String.valueOf(locationMap.get("url")));
            Encounter encounter = new Encounter();
            encounter.setLocationArea(lArea);
            versionList.forEach(v1 -> {
                Map versionMap = (Map) v1;
                EncounterVersionDetail evd = new EncounterVersionDetail();
                    evd.setMaxChance(new BigInteger(String.valueOf(versionMap.get("max_chance"))));
                    Map versionMap1 = (Map) versionMap.get("version");
                    Version ver = new Version();
                        ver.setName(String.valueOf(versionMap1.get("name")));
                        ver.setUrl(String.valueOf(versionMap1.get("url")));
                    evd.setVersion(ver);
                   List listEnc = (List) versionMap.get("encounter_details");
                        listEnc.forEach(v2 -> {
                            Map edMap = (Map) v2;
                            EncounterDetail ed = new EncounterDetail();
                            ed.setChance(new BigInteger(String.valueOf(edMap.get("chance"))));
                            ed.setMaxLevel(new BigInteger(String.valueOf(edMap.get("max_level"))));
                            ed.setMinLevel(new BigInteger(String.valueOf(edMap.get("min_level"))));
                            Version vr = new Version();
                            Map vrMap = (Map) edMap.get("method");
                                vr.setName(String.valueOf(vrMap.get("name")));
                                vr.setUrl(String.valueOf(vrMap.get("url")));
                            ed.setMethod(vr);
                            evd.getEncounterDetails().add(ed);
                        });
                encounter.getVersionDetails().add(evd);
            });
            response.getEncounters().add(encounter);
        });
        rep.save(new Log(this.httpServletRequest.getRemoteAddr(),"getLocalAreaEncounters"));
        return response;
    }


    }