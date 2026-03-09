package ru.otus.vivlev.library.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import ru.otus.vivlev.library.batch.dto.AlbumCsvDto;
import ru.otus.vivlev.library.domain.Album;
import ru.otus.vivlev.library.repository.AlbumRepository;
import ru.otus.vivlev.library.repository.GenreRepository;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    @Bean
    public Job importAlbumsJob(Step importAlbumsStep) {
        return jobBuilderFactory.get("importAlbumsJob")
                .incrementer(new RunIdIncrementer())
                .start(importAlbumsStep)
                .build();
    }

    @Bean
    public Step importAlbumsStep(ItemReader<AlbumCsvDto> reader,
                                  ItemProcessor<AlbumCsvDto, Album> processor,
                                  ItemWriter<Album> writer) {
        return stepBuilderFactory.get("importAlbumsStep")
                .<AlbumCsvDto, Album>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<AlbumCsvDto> albumCsvReader() {
        return new FlatFileItemReaderBuilder<AlbumCsvDto>()
                .name("albumCsvReader")
                .resource(new FileSystemResource("data/albums.csv"))
                .delimited()
                .names("title", "artist", "genreName", "releaseYear", "coverImageUrl")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<AlbumCsvDto>() {{
                    setTargetType(AlbumCsvDto.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<AlbumCsvDto, Album> albumProcessor() {
        return new AlbumItemProcessor(albumRepository, genreRepository);
    }

    @Bean
    public JdbcBatchItemWriter<Album> albumWriter() {
        return new JdbcBatchItemWriterBuilder<Album>()
                .dataSource(dataSource)
                .sql("INSERT INTO album (title, artist, genre_id, release_year, cover_image_url) " +
                     "VALUES (:title, :artist, :genre.id, :releaseYear, :coverImageUrl)")
                .beanMapped()
                .build();
    }
}
