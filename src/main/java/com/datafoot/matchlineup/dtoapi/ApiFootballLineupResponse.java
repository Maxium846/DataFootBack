package com.datafoot.matchlineup.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiFootballLineupResponse {

    private List<ApiFootballLineupItem> response;
}
