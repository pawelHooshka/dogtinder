package com.treat.tinderdog.web;

import com.treat.tinderdog.data.CustomUserDetails;
import com.treat.tinderdog.model.Animal;
import com.treat.tinderdog.model.AnimalsList200Response;
import com.treat.tinderdog.service.PetFinderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProfilesController {

    private final PetFinderService petFinderService;

    public ProfilesController(final PetFinderService petFinderService) {
        this.petFinderService = petFinderService;
    }

    @GetMapping("/profiles")
    @Operation(
            description = "Get new profile suggestions for the user - the way this method works is that" +
                    "you ae expected to provide a limit value as a required parameter. The limit value specifies" +
                    "the number of profiles to be returned. Application also maintains a page number for every user, " +
                    "and so for every request to this method - the requested number of profiles is returned from the " +
                    "database and the page number is increased by 1. This guarantees that consecutive calls will returned " +
                    "always new profiles, unless the service is restarted since for now we are using only a volatile data store." +
                    "Those values for limit and page constitute paging of the request.",
            parameters = {
                    @Parameter(
                            name = "limit",
                            required = true,
                            description = "Specifies the number of results to return",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "gender",
                            description = "Specifies whether you are looking for male or a female",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "breed",
                            description = "Specifies the breed of the dog you are looking for",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "organisation",
                            description = "Specifies the organisation to which the pet belongs",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "location",
                            description = "Specifies the location of the pet",
                            in = ParameterIn.QUERY
                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    public ResponseEntity<AnimalsList200Response> getPetSuggestions(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @RequestParam(required = false) List<String> gender,
            final @RequestParam(required = false) List<String> breed,
            final @RequestParam(required = false) List<String> organisation,
            final @RequestParam(required = false) String location,
            final @RequestParam Integer limit) {
        final Map<String, Object> queryParameters = new HashMap<>();

        queryParameters.put("type", "dog");
        queryParameters.put("limit", limit);

        if (gender != null) {
            queryParameters.put("gender", gender);
        }
        if (breed != null) {
            queryParameters.put("breed", breed);
        }
        if (organisation != null) {
            queryParameters.put("organisation", organisation);
        }
        if (location != null) {
            queryParameters.put("location", location);
        }

        return ResponseEntity.ok(petFinderService.getDogs(userDetails, queryParameters));
    }

    @Operation(
            description = "Get a matched profile by the unique identifier of the match record",
            parameters = {
                    @Parameter(
                            name = "matchId",
                            required = true,
                            description = "This is the id of this match record, it uniquely identifies this match record."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, the body of the response contains" +
                                    "the requested matched profile."

                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Request was processed and rejected since it attempted to access a profile " +
                                    "that did not match with this user - this is forbidden."

                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request was processed and profile was not found."

                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @GetMapping("/matches/{matchId}/profile")
    public ResponseEntity<Animal> getMatchedProfile(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @PathVariable UUID matchId) {
        return ResponseEntity.ok(petFinderService.getMatchedProfile(userDetails, matchId));
    }

    @Operation(
            description = "Get a matched profile by the unique identifier of the like record",
            parameters = {
                    @Parameter(
                            name = "likedId",
                            required = true,
                            description = "This is the id of this like record, it uniquely identifies this like record."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, the body of the response contains" +
                                    "the requested liked profile."

                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Request was processed and rejected since it attempted to access a profile " +
                                    "that did wasn't liked by this user - this is forbidden."

                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request was processed and profile was not found."

                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @GetMapping("/likes/{likeId}/profile")
    public ResponseEntity<Animal> getLikedProfile(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @PathVariable UUID likeId) {
        return ResponseEntity.ok(petFinderService.getLikedProfile(userDetails, likeId));
    }
}
