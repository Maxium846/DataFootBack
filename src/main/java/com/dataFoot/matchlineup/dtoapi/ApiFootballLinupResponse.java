package com.dataFoot.matchlineup.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballLinupResponse {

    private List<ApiFootballLineupItem> response;
}
