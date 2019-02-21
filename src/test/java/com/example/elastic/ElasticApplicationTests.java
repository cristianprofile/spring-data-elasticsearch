package com.example.elastic;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticApplicationTests {

    @Rule
    public OutputCapture capture = new OutputCapture();


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Autowired
    private PersonRepository personRepository;


    @Before
    public void before() {

        elasticsearchTemplate.deleteIndex(Person.class);
        elasticsearchTemplate.createIndex(Person.class);
        elasticsearchTemplate.putMapping(Person.class);
        elasticsearchTemplate.refresh(Person.class);
    }

    @After
    public void after() {
        elasticsearchTemplate.deleteIndex(Person.class);
    }


    @Test
    public void contextLoads() {

        logger.info("Test begin process");
        Iterable<Person> people = personRepository.findAll();
        long total = StreamSupport.stream(people.spliterator(), false).count();

        Person person = new Person();
        person.setId(1L);
        person.setAddress("gran via Street");

        LocalDate birthDate = LocalDate.of(1993, Month.JANUARY, 12);


        person.setBirthDate(birthDate);
        person.setFavoriteColor(ColorEnum.BLUE);
        person.setFavoriteNumber(2);
        person.setName("Juan");
        person.setSurname("Rodriguez");


        Person person2 = new Person();
        person2.setId(2L);
        birthDate = LocalDate.of(1991, Month.JANUARY, 10);
        person2.setBirthDate(birthDate);
        person2.setFavoriteColor(ColorEnum.RED);
        person2.setFavoriteNumber(1);
        person2.setName("laura");
        person2.setSurname("palmer");


        Person person3= new Person();
        person3.setId(3L);
        birthDate = LocalDate.of(1981, Month.FEBRUARY, 10);
        person3.setBirthDate(birthDate);
        person3.setFavoriteColor(ColorEnum.RED);
        person3.setFavoriteNumber(5);
        person3.setName("cris");
        person3.setSurname("gibson");

        personRepository.save(person);
        personRepository.save(person2);
        personRepository.save(person3);


        people = personRepository.findAll();
        long count = StreamSupport.stream(people.spliterator(), false).count();
        assertThat(count).isEqualTo(3);

        Integer[] favoritNumbers = {5,55,66};
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("favoriteNumber", favoritNumbers);

        SearchQuery searchQuery =
               new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(termsQueryBuilder).withPageable(PageRequest.of(0, 10)).
                        build();

        AggregatedPage<Person> people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);
        assertThat(people1.getTotalElements()).isEqualTo(1);
        assertThat(people1.getContent().get(0).getId()).isEqualTo(person3.getId());


        Integer[] favoritNumbers2 = {5,2,66};
        termsQueryBuilder = QueryBuilders.termsQuery("favoriteNumber", favoritNumbers2);



        searchQuery =
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(termsQueryBuilder).withPageable(PageRequest.of(0, 10)).
                        build();
        people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);
        assertThat(people1.getTotalElements()).isEqualTo(2);

        RangeQueryBuilder rangeQueryBuilder =
                rangeQuery("favoriteNumber").from(0).to(6).includeLower(true).includeUpper(true);


        searchQuery =
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(rangeQueryBuilder).withPageable(PageRequest.of(0, 10)).build();

        people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);
        assertThat(people1.getTotalElements()).isEqualTo(3);

        MatchQueryBuilder favoriteNumber = QueryBuilders.matchQuery("favoriteNumber", 2);

        searchQuery =
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(favoriteNumber).withPageable(PageRequest.of(0, 10)).build();

        people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);
        assertThat(people1.getTotalElements()).isEqualTo(1);
        assertThat(people1.getContent().get(0).getId()).isEqualTo(person.getId());

        BoolQueryBuilder nameNumberShould = new BoolQueryBuilder()
                .should(termQuery("favoriteNumber", "544454032116122"))
                .should(termQuery("name", "cris"));


        //create or sentece using should
        BoolQueryBuilder complexQuery = new BoolQueryBuilder().
                must(nameNumberShould);


        searchQuery =
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(complexQuery).withPageable(PageRequest.of(0, 10)).build();

        people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);
        assertThat(people1.getTotalElements()).isEqualTo(1);
        assertThat(people1.getContent().get(0).getId()).isEqualTo(person3.getId());


        //***** MULTIPLE PREDICATES

        MatchQueryBuilder surname = QueryBuilders.matchQuery("surname", "gibson");

        BoolQueryBuilder complexNumberOrNameAndSurname = new BoolQueryBuilder().
                must(nameNumberShould).must(surname);



        searchQuery =
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(complexNumberOrNameAndSurname).withPageable(PageRequest.of(0, 10)).build();

        people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);
        assertThat(people1.getTotalElements()).isEqualTo(1);
        assertThat(people1.getContent().get(0).getId()).isEqualTo(person3.getId());


        BoolQueryBuilder nameNumbeOrNameShould = new BoolQueryBuilder()
                .should(termQuery("favoriteNumber", "544454032116122"))
                .should(termQuery("name", "juan"));

        MatchQueryBuilder favoriteColor = QueryBuilders.matchQuery("favoriteColor", ColorEnum.BLUE.toString());


        // his favorite color   must be 'blue' color and its name must be 'juan'
        complexQuery = new BoolQueryBuilder().must(favoriteColor).must(nameNumbeOrNameShould);

        searchQuery =
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(complexQuery).withPageable(PageRequest.of(0, 10)).build();

        people1 = elasticsearchTemplate.queryForPage(searchQuery, Person.class);


        assertThat(people1.getTotalElements()).isEqualTo(1);
        assertThat(people1.getContent().get(0).getId()).isEqualTo(person.getId());

        //TODO ADD DATE EXAMPLE

        personRepository.delete(person);
        personRepository.delete(person2);
        personRepository.delete(person3);

        people = personRepository.findAll();
        count = StreamSupport.stream(people.spliterator(), false).count();
        assertThat(count).isEqualTo(0);

        logger.info("Test begin process");
        logger.info("Test end process");
        assertThat(capture.toString()).contains("Test begin process");
        assertThat(capture.toString()).contains("Test end process");

    }

}

