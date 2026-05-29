package com.dataFoot.league.dtoapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseDto {

    private List<ResponseItemsDto> response;
}
