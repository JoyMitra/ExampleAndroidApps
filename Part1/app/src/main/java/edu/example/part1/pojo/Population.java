package edu.example.part1.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Joy on 3/19/18.
 * A model to store JSON data retrieved from an Http server
 */

public class Population {
    @SerializedName("worldpopulation")
    public List<WorldPopulation> worldPopulationList = null;

    public class WorldPopulation {
        @SerializedName("rank")
        public Integer rank;

        @SerializedName("country")
        public String country;

        @SerializedName("population")
        public String population;

        @SerializedName("flag")
        public String flag;
    }
}
