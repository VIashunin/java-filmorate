package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final FriendsDbStorage friendsDbStorage;

    private static Genre genre1 = Genre.builder()
            .id(1)
            .name("Комедия")
            .build();
    private static Genre genre2 = Genre.builder()
            .id(2)
            .name("Драма")
            .build();
    private static Genre genre3 = Genre.builder()
            .id(3)
            .name("Мультфильм")
            .build();
    private static Genre genre4 = Genre.builder()
            .id(4)
            .name("Триллер")
            .build();
    private static Genre genre5 = Genre.builder()
            .id(5)
            .name("Документальный")
            .build();
    private static Genre genre6 = Genre.builder()
            .id(6)
            .name("Боевик")
            .build();

    private static Mpa mpa1 = Mpa.builder()
            .id(1)
            .name("G")
            .build();

    private static Mpa mpa2 = Mpa.builder()
            .id(2)
            .name("PG")
            .build();

    private static Mpa mpa3 = Mpa.builder()
            .id(3)
            .name("PG-13")
            .build();

    private static Mpa mpa4 = Mpa.builder()
            .id(4)
            .name("R")
            .build();

    private static Mpa mpa5 = Mpa.builder()
            .id(5)
            .name("NC-17")
            .build();

    private static User firstUser = User.builder()
            .id(1)
            .login("1test")
            .name("1test")
            .email("1test@gmail.com")
            .birthday(LocalDate.of(2000, 1, 1))
            .build();
    private static User secondUser = User.builder()
            .id(2)
            .login("2test")
            .name("2test")
            .email("2test@gmail.com")
            .birthday(LocalDate.of(2000, 1, 1))
            .build();
    private static User thirdUser = User.builder()
            .id(3)
            .login("3test")
            .name("3test")
            .email("3test@gmail.com")
            .birthday(LocalDate.of(2000, 1, 1))
            .build();
    private static Film firstFilm = Film.builder()
            .id(1)
            .name("1test")
            .description("1test")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(60)
            .rate(0)
            .mpa(Mpa.builder().id(1).name("G").build())
            .build();

    private static Film secondFilm = Film.builder()
            .id(1)
            .name("2test")
            .description("2test")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(60)
            .rate(0)
            .mpa(Mpa.builder().id(2).name("PG").build())
            .build();

    @Test
    public void testDatabase() {
        //genres
        List<Genre> allGenres = genreDbStorage.getAllGenres();
        assertThat(allGenres).isEqualTo(List.of(genre1, genre2, genre3, genre4, genre5, genre6));

        //genreId
        Genre genre = genreDbStorage.getGenreById(1);
        assertThat(genre).isEqualTo(genre1);

        //mpa
        List<Mpa> allMpa = mpaDbStorage.getAllMpa();
        assertThat(allMpa).isEqualTo(List.of(mpa1, mpa2, mpa3, mpa4, mpa5));

        //mpaId
        Mpa mpa = mpaDbStorage.getMpaById(1);
        assertThat(mpa).isEqualTo(mpa1);

        //addUser
        User firstUserAdd = userDbStorage.addUser(firstUser);
        User secondUserAdd = userDbStorage.addUser(secondUser);
        User thirdUserAdd = userDbStorage.addUser(thirdUser);
        assertThat(firstUserAdd).isEqualTo(firstUser);
        assertThat(secondUserAdd).isEqualTo(secondUser);
        assertThat(thirdUserAdd).isEqualTo(thirdUser);

        //updateUser
        User firstUserUpdate = userDbStorage.updateUser(firstUser);
        assertThat(firstUserUpdate).isEqualTo(firstUser);

        //getAllUsers
        List<User> allFindedUsers = userDbStorage.getAllUsers();
        List<User> allUsers = List.of(firstUser, secondUser, thirdUser);
        assertThat(allFindedUsers).isEqualTo(allUsers);

        //getUserById
        User firstFindedUser = userDbStorage.getUserById(firstUser.getId());
        assertThat(firstFindedUser).isEqualTo(firstUser);

        //addFriend
        List<User> secondUserAsFriend = friendsDbStorage.addFriend(firstUser.getId(), secondUser.getId());
        assertThat(secondUserAsFriend).isEqualTo(List.of(secondUser));

        //getCommonFriends
        friendsDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
        friendsDbStorage.addFriend(secondUser.getId(), thirdUser.getId());
        List<User> commonFriendsFirstUserSecondUser = friendsDbStorage.getCommonFriends(firstUser.getId(), secondUser.getId());
        assertThat(commonFriendsFirstUserSecondUser).isEqualTo(List.of(thirdUser));

        //getFriendsByUserId
        List<User> friends = friendsDbStorage.getUserFriends(firstUser.getId());
        assertThat(friends).isEqualTo(List.of(secondUser, thirdUser));

        //deleteFriend
        List<User> friendsOfFirstUser = friendsDbStorage.deleteFriend(firstUser.getId(), thirdUser.getId());
        assertThat(friendsOfFirstUser).isEqualTo(List.of(secondUser));

        //addFilm
        Film film = filmDbStorage.addFilm(firstFilm);
        assertThat(film).isEqualTo(firstFilm);

        //getFilmById
        Film film1 = filmDbStorage.getFilmById(1);
        assertThat(film1).isEqualTo(firstFilm);

        //getAllFilms
        filmDbStorage.addFilm(secondFilm);
        List<Film> allFilms = filmDbStorage.getAllFilms();
        assertThat(allFilms).isEqualTo(List.of(firstFilm, secondFilm));
        List<Film> popularFilms = filmDbStorage.getPopularFilms(2);
        assertThat(popularFilms).isEqualTo(List.of(firstFilm, secondFilm));

        //addLike
        Film film1WithLikeFromUser1 = likesDbStorage.addLike(firstFilm.getId(), firstUser.getId());
        assertThat(film1WithLikeFromUser1.getRate()).isEqualTo(1);

        //deleteLike
        Film film1WithoutLikeFromUser1 = likesDbStorage.deleteLike(firstFilm.getId(), firstUser.getId());
        assertThat(film1WithoutLikeFromUser1.getRate()).isEqualTo(0);

        //addGenre
        List<Genre> genreForAddition = List.of(genre5, genre6);
        filmGenreDbStorage.addGenresByFilmId(firstFilm.getId(), genreForAddition);
        assertThat(genreForAddition).isEqualTo(filmGenreDbStorage.getGenresByFilmId(firstFilm.getId()));

        //deleteGenre
        filmGenreDbStorage.deleteGenresByFilmId(firstFilm.getId());
        assertThat(firstFilm.getGenres()).isEqualTo(List.of());
    }
}
