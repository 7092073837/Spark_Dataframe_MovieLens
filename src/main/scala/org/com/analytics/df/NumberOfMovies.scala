package org.com.analytics.df

import Spark_Session._
import org.apache.spark.sql.functions._
import loadSourceData.load_movie

/**
  *
  */
object NumberOfMovies {
    def numOfGenresPerYear(): Unit = {


        val moviesDF = load_movie.createOrReplaceTempView("movie__tbl")
        import spark.implicits._

        val moviesTitleDF = spark.sql("select movieId,substring(title, 1, length(title)-6)as moviesTitleDF," +
          "substring(title,length(title)-4,length(title)-3)as year, genres from movie_tbl ")

        moviesTitleDF.createOrReplaceTempView("drama_movies_tbl")
        val movieDramaTitleYearDF = spark.sql("select movieid ,moviesTitleDF, substring(year,length(year)-4,length(year)-1)as R_year, genres from drama_movies_tbl ")

        println("The list of movies released in associated year: " )
         movieDramaTitleYearDF.show(20)

        val movieDramaFlatGenresDF = movieDramaTitleYearDF.withColumn("genres", explode(split($"genres", "[|]")))

        movieDramaFlatGenresDF.createOrReplaceTempView("final_movie_table")

        val numMoviesYearDF = spark.sql("select R_year,moviesTitleDF,genres,count(*) over (partition by R_year order by genres asc)as num_of_movies from  final_movie_table")

        println("the number of movies per Genre per Year : " + numMoviesYearDF.show(20))

    }
}