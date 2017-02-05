package org.sergiiz.rxkata;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

class CountriesServiceSolved implements CountriesService {

    private final Predicate<Country> countryPredicate = c -> c.population > 1E6;

    @Override
    public Single<String> countryNameInCapitals(Country country) {
        return Single.just(country)
                .map(c -> c.name.toUpperCase())
                .onErrorReturnItem("[NOT AVAILABLE]");
    }

    public Single<Integer> countCountries(List<Country> countries) {
        return Observable.fromIterable(countries)
                .count()
                .map(Long::intValue);
    }

    public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(c -> c.population);
    }

    @Override
    public Observable<String> listNameOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(c -> c.name);
    }

    @Override
    public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
        return Maybe.just(countries.get(3))
                .just(countries.get(4))
                .toObservable();
    }

    @Override
    public Single<Boolean> isPopulationMoreThan1Million(List<Country> countries) {
        return Observable.fromIterable(countries)
                .all(countryPredicate);
    }


    @Override
    public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                .filter(countryPredicate);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillion(FutureTask<List<Country>> countriesFromNetwork) {
        return Observable.fromFuture(countriesFromNetwork)
                .flatMap(this::listPopulationMoreThanOneMillion);
    }

    @Override
    public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
        return Observable.fromIterable(countries)
                .filter(c -> c.name.equals(countryName))
                .map(c -> c.currency)
                .defaultIfEmpty("USD (default)");
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
        return listPopulationOfEachCountry(countries).reduce((c1, c2) -> c1 + c2)
                .toObservable();
    }

    @Override
    public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
        return Observable.zip(listNameOfEachCountry(countries), listPopulationOfEachCountry(countries), Tuple2::new)
                .toMap(k -> k.a, v -> v.b);
    }

    private class Tuple2<A, B> {
        final A a;
        final B b;

        Tuple2(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }
}
