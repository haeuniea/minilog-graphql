package com.asdf.minilog.graphql.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FollowResponse {
    @NonNull private Long followerId;

    @NonNull private Long followeeId;
}
