package com.example.movie_server.service;

import com.example.movie_server.model.*;
import com.example.movie_server.repository.ActorRepository;
import com.example.movie_server.repository.MovieRepository;
import com.example.movie_server.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final WatchlistRepository watchlistRepository;

    public List<Movie> getAll(){
        return movieRepository.findAll();
    }

    public Movie getMovieById(String movieId) {
        return movieRepository.findById(movieId).orElseThrow(() -> new NoSuchElementException("Movie not found with ID: " + movieId));
    }


    public List<Actor> getMovieActors(String movieId) {
        List<ActorRole> roles = getMovieById(movieId).getRoles();

        List<Actor> movieActors = new ArrayList<>();

        for(ActorRole ar: roles){
            movieActors.add(actorRepository.findById(ar.getActorId()).orElseThrow(() -> new NoSuchElementException("Actor not found with ID: " + ar.getActorId())));
        }

        return movieActors;
    }


    public Movie saveMovie(Movie movie) {
        if (movie == null ||
                movie.getImage() == null || movie.getImage().isEmpty() ||
                movie.getDescription() == null || movie.getDescription().isEmpty() ||
                movie.getTitle() == null || movie.getTitle().isEmpty() ||
                movie.getReleaseDate() == null || movie.getReleaseDate().isEmpty() ||
                movie.getGenre() == null) {
            throw new IllegalArgumentException("Movie fields cannot be empty");
        }
        return movieRepository.save(movie);
    }

    public boolean isInWatchlist(String movieId) {
        User currentlyLoggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Watchlist watchlist = watchlistRepository.findWatchlistByUser_Id(currentlyLoggedInUser.getId());
        List<Movie> movies = watchlist.getMovies();
        Movie movie = getMovieById(movieId);
        return movies.contains(movie);
    }

    public Movie updateMovie(String movieId, Movie movie) {
        if (movie == null ||
                movie.getImage() == null || movie.getImage().isEmpty() ||
                movie.getDescription() == null || movie.getDescription().isEmpty() ||
                movie.getTitle() == null || movie.getTitle().isEmpty() ||
                movie.getReleaseDate() == null || movie.getReleaseDate().isEmpty() ||
                movie.getGenre() == null) {
            throw new IllegalArgumentException("Movie fields cannot be empty");
        }
        Movie existingMovie = movieRepository.findById(movieId).orElseThrow(() -> new NoSuchElementException("Movie not found with ID: " + movieId));

        existingMovie.setDescription(movie.getDescription());
        existingMovie.setImage(movie.getImage());
        existingMovie.setTitle(movie.getTitle());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        existingMovie.setRoles(movie.getRoles());
        existingMovie.setGenre(movie.getGenre());

        return movieRepository.save(existingMovie);
    }

    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findMoviesByTitleLike(title);
    }

    public Page<Movie> getAll(Pageable pageable){
        return movieRepository.findAll(pageable);
    }

    public Page<Movie> searchMoviesByGenre(Genre genre, Pageable pageable) {
        return movieRepository.findMoviesByGenre(genre, pageable);
    }
}
