package com.treat.tinderdog.web;

import com.treat.tinderdog.data.CustomUserDetails;
import com.treat.tinderdog.data.UserLike;
import com.treat.tinderdog.data.UserMatch;
import com.treat.tinderdog.model.UserLikeWithPossibleMatchResponse;
import com.treat.tinderdog.model.UserLikes;
import com.treat.tinderdog.model.UserMatches;
import com.treat.tinderdog.service.UserService;

import com.treat.tinderdog.service.exception.PagingOrSortingPropertiesInvalidException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @Operation(
            description = "Get the likes of the user - this endpoint will return a collection of objects representing " +
                    "users that the logged in user liked. Every object in the response will basically contain " +
                    "logged in user id and the user id of the user this user liked.",
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Optional (REQUIRED if limit parameter is present) page number to retrieve results from"
                    ),
                    @Parameter(
                            name = "limit",
                            description = "Optional (REQUIRED if page parameter is present) size of the page"
                    ),
                    @Parameter(
                            name = "sortProperty",
                            description = "Optional (REQUIRED if sortDirection parameter is present) name " +
                                    "of the property to sort by - by default, sorting will be done in the ascending " +
                                    "direction unless sortDirection parameter is specified also, in which case - sorting " +
                                    "will be performed in the direction specified by the sortDirection parameter. " +
                                    "If sortProperty parameter is not present - no sorting will be attempted."
                    ),
                    @Parameter(
                            name = "sortDirection",
                            description = "Optional direction of the sort if sortProperty parameter is present."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, the body of the response wll contain" +
                                    "logged in user's likes or an empty collection. Every returned like object will also" +
                                    "have a unique (UUID) likeId which will uniquely identify that likes"

                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "This request cannot be processed if: </br>" +
                                    "- page is given but without a limit </br>" +
                                    "- limit is given but without a page </br>" +
                                    "- sortDirection is given but without sortProperty </br>"
                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @GetMapping("/likes")
    public ResponseEntity<UserLikes> getLikes(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @RequestParam(required = false) Integer page,
            final @RequestParam(required = false) Integer limit,
            final @RequestParam(required = false) String sortProperty,
            final @RequestParam(required = false) String sortDirection) {
        if (page == null) {
            return ResponseEntity.ok(userService.getUserLikes(userDetails));
        } else {
            validateForPaging(userDetails, page, limit, sortProperty, sortDirection);
            return ResponseEntity.ok(userService.getUserLikes(userDetails, page, limit, sortProperty, sortDirection));
        }
    }

    @Operation(
            description = "Create a new like - this operation will create a new like entry in the database and if " +
                    "there is a mutual like it will also create a match",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, the body of the response wll contain: </br>" +
                                    "- unique likeId - uniquely identifying this like entry </br> " +
                                    "- logged in userId - a unique id of this user </br> " +
                                    "- id of the liked user likedUserId - uniquely identifying the user that was liked </br> " +
                                    "- time when this like was created likeCreatedAt </br> " +
                                    "- possible matchId uniquely identifying the new match (if any) </br> " +
                                    "- time when this match was created matchCreatedAt </br>"

                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User attempted to like their own profile - it should not happen (CONFLICT)"

                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @PutMapping("/likes/{profileId}")
    public ResponseEntity<UserLikeWithPossibleMatchResponse> likeProfile(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @PathVariable("profileId") Long profileId) {
        return ResponseEntity.ok(userService.likeProfile(userDetails, profileId));
    }

    @Operation(
            description = "Hard delete operation! I am aware that in a production level system a soft delete would " +
                    "perhaps be better, but since this is only to demonstrate - I opted to leave as it is. " +
                    "This operation will delete a specific like of this user identified by the unique likeId " +
                    "and potentially also a related match - in case of a mutual match.",
            parameters = {
                    @Parameter(
                            name = "likeId",
                            required = true,
                            in = ParameterIn.PATH,
                            description = "This is the unique identifier of the user's like (UUID, not a user's userId)."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, all the likes of this user are now" +
                                    "deleted, accordingly all the matches are also deleted from the database."

                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Request was processed but the like entry with the given likeId was not found " +
                                    "in the database."
                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<UserLike> deleteLike(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @PathVariable("likeId") UUID likeId) {
        return ResponseEntity.ok(userService.unlikeProfile(userDetails, likeId));
    }

    @Operation(
            description = "Delete all the likes of this user - this operation will delete all the likes that this user has " +
                    "ever liked and all the matches",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, all the likes of this user are now" +
                                    "deleted, accordingly all the matches are also deleted from the database"

                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @DeleteMapping("/likes")
    public ResponseEntity deleteAllLikes(final @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.unlikeAll(userDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Get the matches of the user - this endpoint will return a collection of objects representing " +
                    "the user's matches. Every object in the response will basically contain " +
                    "logged in user id and the user id of the user this user liked or was liked by. " +
                    "Response will contain essentially only mutual likes",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, the body of the response wll contain" +
                                    "logged in user's matches"

                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @GetMapping("/matches")
    public ResponseEntity<UserMatches> getMatches(
            final @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserMatches(userDetails));
    }

    @Operation(
            description = "Get a specific match record based on this matchId - unique identifier of the match record (UUID)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request was processed without an incident, the body of the response contains" +
                                    "the requested match record."

                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Request was processed and rejected since it attempted to access a record " +
                                    "that did not contain this user's own userId - this is forbidden."

                    )
            },
            security = @SecurityRequirement(name = "JSESSIONID")
    )
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<UserMatch> getMatch(
            final @AuthenticationPrincipal CustomUserDetails userDetails,
            final @PathVariable("matchId") UUID matchId) {
        return ResponseEntity.ok(userService.getUserMatch(userDetails, matchId));
    }

    private void validateForPaging(final CustomUserDetails userDetails,
                                   final Integer page,
                                   final Integer limit,
                                   final String sortDirection,
                                   final String sortProperty) {
        if (pagingInformationInvalid(page, limit)
                || sortingInformationInvalid(sortProperty, sortDirection)) {
            throw new PagingOrSortingPropertiesInvalidException(
                    userDetails.getUsername(), page, limit, sortProperty, sortDirection);
        }
    }

    private boolean pagingInformationInvalid(final Integer page, final Integer limit) {
        return (page == null && limit != null) || (page != null && limit == null);
    }

    private boolean sortingInformationInvalid(final String sortProperty, final String sortDirection) {
        return sortProperty == null && sortDirection != null;
    }
}
