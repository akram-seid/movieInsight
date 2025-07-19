package com.seidbros.movieinsight.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
   private String id;
   private List<Cast> cast ;
   private List<Crew> crew ;

}
