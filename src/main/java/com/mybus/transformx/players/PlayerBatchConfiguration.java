package com.mybus.transformx.players;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class PlayerBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${file.input}")
    private String fileInput;

    @Bean
    public FlatFileItemReader<Player> reader() {
        return new FlatFileItemReaderBuilder<Player>().name("playerItemReader")
                .resource(new ClassPathResource(fileInput))
                .delimited()
                .names(new String[] { "player_id", "player_url", "fifa_version", "fifa_update", "fifa_update_date", "short_name", "long_name", "player_positions", "overall",
                        "potential", "value_eur", "wage_eur", "age", "dob", "height_cm", "weight_kg", "league_id", "league_name", "league_level", "club_team_id", "club_name",
                        "club_position", "club_jersey_number", "club_loaned_from", "club_joined_date", "club_contract_valid_until_year", "nationality_id", "nationality_name",
                        "nation_team_id", "nation_position", "nation_jersey_number", "preferred_foot", "weak_foot", "skill_moves", "international_reputation", "work_rate",
                        "body_type", "real_face", "release_clause_eur", "player_tags", "player_traits", "pace", "shooting", "passing", "dribbling", "defending", "physic",
                        "attacking_crossing", "attacking_finishing", "attacking_heading_accuracy", "attacking_short_passing", "attacking_volleys", "skill_dribbling",
                        "skill_curve", "skill_fk_accuracy", "skill_long_passing", "skill_ball_control", "movement_acceleration", "movement_sprint_speed", "movement_agility",
                        "movement_reactions", "movement_balance", "power_shot_power", "power_jumping", "power_stamina", "power_strength", "power_long_shots", "mentality_aggression",
                        "mentality_interceptions", "mentality_positioning", "mentality_vision", "mentality_penalties", "mentality_composure", "defending_marking_awareness",
                        "defending_standing_tackle", "defending_sliding_tackle", "goalkeeping_diving", "goalkeeping_handling", "goalkeeping_kicking", "goalkeeping_positioning",
                        "goalkeeping_reflexes", "goalkeeping_speed", "ls", "st", "rs", "lw", "lf", "cf", "rf", "rw", "lam", "cam", "ram", "lm", "lcm", "cm", "rcm", "rm", "lwb",
                        "ldm", "cdm", "rdm", "rwb", "lb", "lcb", "cb", "rcb", "rb", "gk", "player_face_url" })
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Player>() {{
                    setTargetType(Player.class);
                }})
                .build();
    }

    @Bean
    public PlayerItemProcessor processor() {
        return new PlayerItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<OutPlayer> writer() {
        FlatFileItemWriter<OutPlayer> flatFileItemWriter = new FlatFileItemWriter<>();
        String exportFileHeader = "player_id, short_name, long_name, player_positions, overall, " +
                "potential, value_eur, wage_eur, age, dateOfBirth, height_cm, weight_kg, league_id, league_name, league_level, club_team_id, club_name, " +
                "club_position, club_jersey_number, club_loaned_from, club_joined_date, club_contract_valid_until_year, nationality_id, nationality_name, " +
                "nation_team_id, nation_position, nation_jersey_number, preferred_foot, weak_foot, skill_moves, international_reputation, work_rate, " +
                "body_type, real_face, release_clause_eur, player_tags, player_traits, pace, shooting, passing, dribbling, defending, physic, " +
                "attacking_crossing, attacking_finishing, attacking_heading_accuracy, attacking_short_passing, attacking_volleys, skill_dribbling, " +
                "skill_curve, skill_fk_accuracy, skill_long_passing, skill_ball_control, movement_acceleration, movement_sprint_speed, movement_agility, " +
                "movement_reactions, movement_balance, power_shot_power, power_jumping, power_stamina, power_strength, power_long_shots, mentality_aggression, " +
                "mentality_interceptions, mentality_positioning, mentality_vision, mentality_penalties, mentality_composure, defending_marking_awareness, " +
                "defending_standing_tackle, defending_sliding_tackle, goalkeeping_diving, goalkeeping_handling, goalkeeping_kicking, goalkeeping_positioning, " +
                "goalkeeping_reflexes, goalkeeping_speed";
        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);
        flatFileItemWriter.setHeaderCallback(headerWriter);
        flatFileItemWriter.setResource(new FileSystemResource("target-output.csv"));
        flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<OutPlayer>() {
                    {
                        setNames(new String[] {"player_id", "short_name", "long_name", "player_positions", "overall",
                                "potential", "value_eur", "wage_eur", "age", "dateOfBirth", "height_cm", "weight_kg", "league_id", "league_name", "league_level", "club_team_id", "club_name",
                                "club_position", "club_jersey_number", "club_loaned_from", "club_joined_date", "club_contract_valid_until_year", "nationality_id", "nationality_name",
                                "nation_team_id", "nation_position", "nation_jersey_number", "preferred_foot", "weak_foot", "skill_moves", "international_reputation", "work_rate",
                                "body_type", "real_face", "release_clause_eur", "player_tags", "player_traits", "pace", "shooting", "passing", "dribbling", "defending", "physic",
                                "attacking_crossing", "attacking_finishing", "attacking_heading_accuracy", "attacking_short_passing", "attacking_volleys", "skill_dribbling",
                                "skill_curve", "skill_fk_accuracy", "skill_long_passing", "skill_ball_control", "movement_acceleration", "movement_sprint_speed", "movement_agility",
                                "movement_reactions", "movement_balance", "power_shot_power", "power_jumping", "power_stamina", "power_strength", "power_long_shots", "mentality_aggression",
                                "mentality_interceptions", "mentality_positioning", "mentality_vision", "mentality_penalties", "mentality_composure", "defending_marking_awareness",
                                "defending_standing_tackle", "defending_sliding_tackle", "goalkeeping_diving", "goalkeeping_handling", "goalkeeping_kicking", "goalkeeping_positioning",
                                "goalkeeping_reflexes", "goalkeeping_speed"});
                    }
                });
            }
        });
        return flatFileItemWriter;
    }

    @Bean
    public Job myBusJob(PlayerJobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("myBusJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step myBusStep() {
        return stepBuilderFactory.get("myBusStep")
                .<Player, OutPlayer> chunk(50000)
                .reader(reader())//E
                .processor(processor())//T
                .writer(writer())//L
                .taskExecutor(taskExecutor())
                .throttleLimit(5)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("myBusTaskExecutor");
    }

}
