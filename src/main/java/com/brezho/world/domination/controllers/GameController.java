package com.brezho.world.domination.controllers;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import com.brezho.world.domination.game.*;
import com.brezho.world.domination.models.ERole;
import com.brezho.world.domination.models.Role;
import com.brezho.world.domination.models.User;
import com.brezho.world.domination.payload.request.*;
import com.brezho.world.domination.payload.response.*;
import com.brezho.world.domination.repository.*;
import com.brezho.world.domination.validator.GameValidator;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.ObjectError;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameParametersRepository gameParametersRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PlayerRoleRepository playerRoleRepository;

    @Autowired
    SanctionRepository sanctionRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamStatusRepository teamStatusRepository;

    @Autowired
    RoundStatusRepository roundStatusRepository;

    @Autowired
    NegotiationRepository negotiationRepository;

    @Autowired
    NegotiationStatusRepository negotiationStatusRepository;

    @GetMapping("/games")
    public ResponseEntity<List<GameResponse>> getAllGames() {
        try {
            List<Game> games = new ArrayList<>();
            gameRepository.findByRoundNum(0).forEach(games::add);

            if (games.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<GameResponse> gameResponses = new ArrayList<>();
            for (Game game : games) {
                GameResponse gameResponse = new GameResponse(game.getId(), game.getTitle(), game.getNumTeams());
                gameResponses.add(gameResponse);
            }

            return new ResponseEntity<>(gameResponses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/create-game")
    public GameCreateDefaultResponse createGameContent() {
        return new GameCreateDefaultResponse();
    }

    @PostMapping("/create-game")
    public ResponseEntity<?> createGame(@RequestBody GameCreateRequest request, Principal principal) {
        try {
            // Проверка GameCreateRequest
            boolean isValidRequest = GameValidator.validateGameCreateRequest(request);
            if (!isValidRequest) {
                return new ResponseEntity<>("The names of teams and units within teams must not be repeated.", HttpStatus.BAD_REQUEST);
            }

            //тут с влальцем захардкожено будет
            Long paramsId = request.getParamsId();
            if (paramsId == null) {
                // Обработка отсутствия paramsId
                return new ResponseEntity<>("paramsId is required.", HttpStatus.BAD_REQUEST);
            }


            // Получение параметров игры по идентификатору
            GameParameters parameters = gameParametersRepository.findById(paramsId).orElse(null);

            if (parameters == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Получение текущего пользователя по его имени (principal.getName())
            User host = userRepository.findByUsername(principal.getName()).orElse(null);

            if (host == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (!"Brezho".equals(parameters.getOwner()) && !parameters.getOwner().equals(host.getUsername())) {
                return new ResponseEntity<>("Invalid owner of parameters.", HttpStatus.BAD_REQUEST);
            }

            Game game = new Game();
            game.setGameCode(request.getGameCode());
            game.setTitle(request.getTitle());
            game.setHost(host);
            game.setParam(parameters);
            game.setNumTeams(request.getNumTeams());

            game.setRoundNum(0);
            RoundStatus initialStatus = roundStatusRepository.findByName(ERoundStatus.GENERAL_ROUND)
                    .orElseThrow(() -> new RuntimeException("Error: Status is not found."));
            game.setRoundStatus(initialStatus);
            game.setEcoLevelCurrentRound(parameters.getInitialEcologyLevel());
            game.setEcoLevelNextRound(parameters.getInitialEcologyLevel());

            // Установка текущего и следующего уровня экологии
            double initialEcoLevel = parameters.getInitialEcologyLevel();
            game.setEcoLevelCurrentRound(initialEcoLevel);
            game.setEcoLevelNextRound(initialEcoLevel);

            Game savedGame = gameRepository.save(game); // Сохранение игры

            List<Team> teams = new ArrayList<>();
            if (request.getTeams() != null) {
                for (TeamRequest teamRequest : request.getTeams()) {
                    Team team = new Team();
                    team.setTeamName(teamRequest.getTeamName());

                    List<Unit> units = new ArrayList<>();
                    if (teamRequest.getUnits() != null) {
                        for (UnitRequest unitRequest : teamRequest.getUnits()) {
                            Unit unit = new Unit();
                            unit.setUnitName(unitRequest.getName());
                            unit.setLevelOfDevelopment(unitRequest.getDevLevel());
                            unit.setLevelOfDevelopmentNext(unitRequest.getDevLevel());

                            Unit savedUnit = unitRepository.save(unit);
                            units.add(savedUnit);
                        }
                    }
                    team.setUnits(units);
                    int initialBudget = parameters.getInitialBudget();
                    team.setBudget(initialBudget);
                    TeamStatus teamStatus = teamStatusRepository.findByName(ETeamStatus.IN_GAME)
                            .orElseThrow(() -> new RuntimeException("Error: Team status is not found."));
                    team.setTeamStatus(teamStatus);
                    team.setGame(savedGame); // Установка game_id в Team, используя сохраненную игру
                    Team savedTeam = teamRepository.save(team);
                    teams.add(savedTeam);
                }
                savedGame.setTeams(teams); // Установка списка команд в сохраненной игре
            }
            GameResponse gameResponse = new GameResponse(savedGame.getId(), savedGame.getTitle(), savedGame.getNumTeams());

            return new ResponseEntity<>(gameResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/create-game/parameters")
    public ResponseEntity<List<GameParameters>> getAllGameParameters(@RequestParam(required = false) String title, Principal principal) {
        try {
            List<GameParameters> parameters = new ArrayList<>();

            if (title == null)
                gameParametersRepository.findAll().forEach(parameters::add);
            else
                gameParametersRepository.findByTitleContaining(title).forEach(parameters::add);

            // Фильтруем параметры по имени пользователя или значению "Brezho"
            String username = principal.getName();
            List<GameParameters> filteredParameters = parameters.stream()
                    .filter(param -> param.getOwner().equals("Brezho") || param.getOwner().equals(username))
                    .collect(Collectors.toList());

            if (filteredParameters.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filteredParameters, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-game/parameters")
    public ResponseEntity<?> createGameParameters(@Valid @RequestBody GameParametersRequest request, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, возвращаем список сообщений об ошибках
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        String owner = request.getOwner();
        if (owner == null) {
            owner = principal.getName();
        }

        String title = request.getTitle();

        // Проверяем, существуют ли параметры с таким owner и title в базе данных
        boolean parametersExist = gameParametersRepository.existsByOwnerAndTitle(owner, title);
        if (parametersExist) {
            return new ResponseEntity<>("Parameters with the same owner and title already exist", HttpStatus.BAD_REQUEST);
        }
        try {
            GameParameters params = new GameParameters(
                    title,
                    owner,
                    request.getNumRounds(),
                    request.getInitialEcologyLevel(),
                    request.getInitialCapital(),
                    request.getNetCityIncome(),
                    request.getIncomeChanges(),
                    request.getDevelopmentCost(),
                    request.getShieldCost(),
                    request.getNuclearTechCost(),
                    request.getEcoProgramCost(),
                    request.getBombCost(),
                    request.getNumBombsProduced(),
                    request.getBombStorageLimit(),
                    request.getEcoProgramImpactOnEcology(),
                    request.getNuclearTechImpactOnEcology(),
                    request.getBombConstructionImpactOnEcology(),
                    request.getBombDropImpactOnEcology(),
                    request.getNumAcceptedSent(),
                    request.getNumRequestsSent(),
                    request.getDevelopmentChange(),
                    request.getNumUnits()
            );

            GameParameters _params = gameParametersRepository.save(params);

            return new ResponseEntity<>(_params, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/join-game/{id}")
    public ResponseEntity<JoinGameResponse> getJoinGame(@PathVariable("id") Long gameId) {
        try {
            // Получение информации о доступных командах и игроках из репозитория
            List<Team> teams = teamRepository.findByGameId(gameId);

            // Создание списка объектов TeamJoinResponse для каждой команды
            List<TeamJoinResponse> teamResponses = new ArrayList<>();
            for (Team team : teams) {
                List<String> players = new ArrayList<>();
                String captain = null;

                for (Player player : team.getPlayers()) {
                    if (player.getPlayerRole().getName() == EPlayerRole.ROLE_CAPTAIN) {
                        captain = player.getUser().getUsername();
                    } else {
                        players.add(player.getUser().getUsername());
                    }
                }

                teamResponses.add(new TeamJoinResponse(team.getId(), team.getTeamName(), captain, players));
            }


            // Создание объекта JoinGameResponse и установка списка команд
            JoinGameResponse response = new JoinGameResponse(teamResponses);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/join-game/{id}")
    public ResponseEntity<String> joinGame(@PathVariable Long id, @RequestBody JoinGameRequest joinRequest, Principal principal) {
        // Проверка, что пользователь существует
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Проверка, что игра существует
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>("Game not found", HttpStatus.NOT_FOUND);
        }

        // Проверка, что игра еще не началась
        if (game.getRoundNum() > 0) {
            return new ResponseEntity<>("Game has already started", HttpStatus.BAD_REQUEST);
        }

        // Получение команды игры, к которой пользователь будет присоединяться
        Team team = game.getTeams().stream()
                .filter(t -> t.getId().equals(joinRequest.getTeamId()))
                .findFirst()
                .orElse(null);

        // Проверка, что команда существует
        if (team == null) {
            return new ResponseEntity<>("Team not found", HttpStatus.NOT_FOUND);
        }

        // Проверка, что команда не заполнена
    /*if (team.getPlayers().size() >= team.getNumberOfPlayers()) {
        return new ResponseEntity<>("Team is full", HttpStatus.BAD_REQUEST);
    }*/

        // Проверка, что в команде нет капитана
        if (joinRequest.getPlayerRole().equals("ROLE_CAPTAIN")) {
            boolean hasCaptain = team.getPlayers().stream()
                    .anyMatch(p -> p.getPlayerRole().getName() == EPlayerRole.ROLE_CAPTAIN);
            if (hasCaptain) {
                return new ResponseEntity<>("Team already has a captain", HttpStatus.BAD_REQUEST);
            }
        }
        Player player = new Player();
        player.setUser(user);

        // Проверка, что игрок не находится уже в этой команде
        boolean isAlreadyInTeam = team.getPlayers().stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()));
        if (isAlreadyInTeam) {
            return new ResponseEntity<>("Player is already in the team", HttpStatus.BAD_REQUEST);
        }
        // Проверка, что игрок не находится уже в другой команде
        boolean isInAnotherTeam = game.getTeams().stream()
                .anyMatch(t -> t.getPlayers().stream()
                        .anyMatch(p -> p.getUser().getId().equals(user.getId())));
        if (isInAnotherTeam) {
            return new ResponseEntity<>("Player is already in another team", HttpStatus.BAD_REQUEST);
        }

        // Создание игрока и присоединение к команде
        PlayerRole playerRole;
        if (joinRequest.getPlayerRole().equals("ROLE_CAPTAIN")) {
            playerRole = playerRoleRepository.findByName(EPlayerRole.ROLE_CAPTAIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            playerRole = playerRoleRepository.findByName(EPlayerRole.ROLE_PLAYER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }
        player.setPlayerRole(playerRole);

        Player savedPlayer = playerRepository.save(player);
        //playerRepository.save(player);

        team.getPlayers().add(player);
        teamRepository.save(team);

        return new ResponseEntity<>("Player joined the game", HttpStatus.OK);
    }

    @DeleteMapping("/join-game/{id}")
    @Transactional
    public ResponseEntity<String> leaveGame(@PathVariable Long id, Principal principal) {
        // Проверка, что пользователь существует
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Проверка, что игра существует
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>("Game not found", HttpStatus.NOT_FOUND);
        }

        // Проверка, что игра еще не началась
        if (game.getRoundNum() > 0) {
            return new ResponseEntity<>("Game has already started", HttpStatus.BAD_REQUEST);
        }

        // Проверка, что пользователь находится в команде игры
        boolean isUserInGame = game.getTeams().stream()
                .flatMap(team -> team.getPlayers().stream())
                .anyMatch(player -> player.getUser().equals(user));

        if (!isUserInGame) {
            return new ResponseEntity<>("User is not part of the game", HttpStatus.BAD_REQUEST);
        }

        // Удаление игрока из команды
        game.getTeams().forEach(team -> team.getPlayers().removeIf(player -> player.getUser().equals(user)));

        // Удаление игрока из таблицы игроков
        playerRepository.deleteByUser(user);

        // Сохранение изменений
        gameRepository.save(game);

        return new ResponseEntity<>("Player left the game", HttpStatus.OK);
    }

    @PutMapping("/start-game/{id}")
    public ResponseEntity<String> startGame(@PathVariable Long id, Principal principal) {
        // Check if the host is starting the game
        User host = userRepository.findByUsername(principal.getName()).orElse(null);
        if (host == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Check if the game exists
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return new ResponseEntity<>("Game not found", HttpStatus.NOT_FOUND);
        }

        // Check if the host is the owner of the game
        if (!game.getHost().equals(host)) {
            return new ResponseEntity<>("Only the host can start the game", HttpStatus.BAD_REQUEST);
        }

        // Check if all teams have a captain
        boolean allTeamsHaveCaptain = game.getTeams().stream()
                .allMatch(team -> team.getPlayers().stream()
                        .anyMatch(player -> player.getPlayerRole().getName() == EPlayerRole.ROLE_CAPTAIN));
        if (!allTeamsHaveCaptain) {
            return new ResponseEntity<>("Cannot start the game - not all teams have a captain", HttpStatus.BAD_REQUEST);
        }

        // Check if all teams have at least two players (captain + players)
        boolean allTeamsHaveEnoughPlayers = game.getTeams().stream()
                .allMatch(team -> team.getPlayers().size() >= 2);
        if (!allTeamsHaveEnoughPlayers) {
            return new ResponseEntity<>("Cannot start the game - not all teams have enough players", HttpStatus.BAD_REQUEST);
        }

        // Update the round number and round status
        game.setRoundNum(1);
        RoundStatus generalRoundStatus = roundStatusRepository.findByName(ERoundStatus.GENERAL_ROUND)
                .orElseThrow(() -> new RuntimeException("Error: Round status not found."));
        game.setRoundStatus(generalRoundStatus);

        // Save the changes
        gameRepository.save(game);

        return new ResponseEntity<>("Game started", HttpStatus.OK);
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<AllStatsForTeamsResponse> getGameStatistics(@PathVariable Long id, Principal principal) {
        try {
            Game game = gameRepository.findById(id).orElse(null);
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check if the authenticated user is a player in the game or the host
            String authenticatedUsername = principal.getName();
            boolean isPlayerOrHost = game.getTeams().stream()
                    .flatMap(team -> team.getPlayers().stream())
                    .map(Player::getUser)
                    .map(User::getUsername)
                    .anyMatch(username -> username.equals(authenticatedUsername))
                    || game.getHost().getUsername().equals(authenticatedUsername);

            if (!isPlayerOrHost) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            List<TeamStatsSharedResponse> teamStatsResponses = new ArrayList<>();
            for (Team team : game.getTeams()) {
                List<UnitSharedResponse> unitResponses = new ArrayList<>();
                for (Unit unit : team.getUnits()) {
                    UnitSharedResponse unitResponse = new UnitSharedResponse(unit.getId(), unit.getUnitName(), unit.isDestroyed());
                    unitResponses.add(unitResponse);
                }

                TeamStatsSharedResponse teamStatsResponse = new TeamStatsSharedResponse(team.getId(), team.getTeamName(), unitResponses, team.getDevLevel());
                teamStatsResponses.add(teamStatsResponse);
            }

            AllStatsForTeamsResponse allStatsResponse = new AllStatsForTeamsResponse(teamStatsResponses, game.getEcoLevelCurrentRound(), game.getRoundNum());

            return new ResponseEntity<>(allStatsResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/game/{gameId}/team/{teamId}")
    public ResponseEntity<TeamStatsResponse> getTeamStatistics(@PathVariable Long gameId, @PathVariable Long teamId, Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check if the authenticated user is a player in the game or the host
            String authenticatedUsername = principal.getName();
            boolean isPlayerOrHost = game.getTeams().stream()
                    .filter(team -> team.getId().equals(teamId))
                    .flatMap(team -> team.getPlayers().stream())
                    .map(Player::getUser)
                    .map(User::getUsername)
                    .anyMatch(username -> username.equals(authenticatedUsername))
                    || game.getHost().getUsername().equals(authenticatedUsername);

            if (!isPlayerOrHost) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Team team = game.getTeams().stream()
                    .filter(t -> t.getId().equals(teamId))
                    .findFirst()
                    .orElse(null);

            if (team == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<UnitResponse> unitResponses = new ArrayList<>();
            for (Unit unit : team.getUnits()) {
                UnitResponse unitResponse = new UnitResponse(
                        unit.getId(),
                        unit.getUnitName(),
                        unit.getLevelOfDevelopment(),
                        unit.isShieldPresence(),
                        unit.isDestroyed()
                );
                unitResponses.add(unitResponse);
            }

            List<String> teamNames = game.getTeams().stream()
                    .map(Team::getTeamName)
                    .collect(Collectors.toList());

            // Get previous round number
            int previousRoundNum = game.getRoundNum() - 1;

            // Get the sanctioning teams for the target team in the previous round
            /*List<String> sanctioningTeams = game.getSanctions().stream()
                    .filter(sanction -> sanction.getRoundNumber() == previousRoundNum && sanction.getTargetTeam().equals(team))
                    .map(Sanction::getIssuingTeam)
                    .map(Team::getTeamName)
                    .collect(Collectors.toList());
*/
            List<Sanction> sanctions = sanctionRepository.findByTargetTeamAndRoundNumber(team, previousRoundNum);
            List<String> sanctioningTeams = new ArrayList<>();

            for (Sanction sanction : sanctions) {
                Team issuingTeam = sanction.getIssuingTeam();
                String issuingTeamName = issuingTeam.getTeamName();
                sanctioningTeams.add(issuingTeamName);
            }

            List<TeamStatsSharedResponse> teamStatsResponses = new ArrayList<>();
            for (Team team_ : game.getTeams()) {
                List<UnitSharedResponse> unitResponses_ = new ArrayList<>();
                for (Unit unit : team_.getUnits()) {
                    UnitSharedResponse unitResponse = new UnitSharedResponse(unit.getId(), unit.getUnitName(), unit.isDestroyed());
                    unitResponses_.add(unitResponse);
                }

                TeamStatsSharedResponse teamStatsResponse = new TeamStatsSharedResponse(team_.getId(), team_.getTeamName(), unitResponses_, team_.getDevLevel());
                teamStatsResponses.add(teamStatsResponse);
            }

            AllStatsForTeamsResponse allStatsResponse = new AllStatsForTeamsResponse(teamStatsResponses, game.getEcoLevelCurrentRound(), game.getRoundNum());

            TeamStatsResponse teamStatsResponse = new TeamStatsResponse(
                    team.getId(),
                    team.getTeamName(),
                    unitResponses,
                    team.isNuclearTech(),
                    team.getNumberOfBombs(),
                    team.getBudget(),
                    sanctioningTeams,
                    allStatsResponse
            );

            return new ResponseEntity<>(teamStatsResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/game/{gameId}/team/{teamId}/trans")
    public ResponseEntity<List<TeamInfoResponse>> getTeamNamesForTrans(@PathVariable Long gameId, @PathVariable Long teamId, Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check if the authenticated user is a player in the specified team with the role ROLE_CAPTAIN
            String authenticatedUsername = principal.getName();
            boolean isCaptain = game.getTeams().stream()
                    .filter(team -> team.getId().equals(teamId))
                    .flatMap(team -> team.getPlayers().stream())
                    .filter(player -> player.getUser().getUsername().equals(authenticatedUsername))
                    .map(Player::getPlayerRole)
                    .map(PlayerRole::getName)
                    .anyMatch(roleName -> roleName == EPlayerRole.ROLE_CAPTAIN);

            if (!isCaptain) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            List<TeamInfoResponse> teamInfos = game.getTeams().stream()
                    .filter(team -> !team.getId().equals(teamId))
                    .map(team -> new TeamInfoResponse(team.getId(), team.getTeamName()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(teamInfos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/game/{gameId}/team/{teamId}/neg-res")
    public ResponseEntity<List<String>> getTeamNamesForNegotiations(@PathVariable Long gameId, @PathVariable Long teamId, Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check if the authenticated user is a player in the specified team with the role ROLE_CAPTAIN
            String authenticatedUsername = principal.getName();
            boolean isCaptain = game.getTeams().stream()
                    .filter(team -> team.getId().equals(teamId))
                    .flatMap(team -> team.getPlayers().stream())
                    .filter(player -> player.getUser().getUsername().equals(authenticatedUsername))
                    .map(Player::getPlayerRole)
                    .map(PlayerRole::getName)
                    .anyMatch(roleName -> roleName == EPlayerRole.ROLE_CAPTAIN);

            if (!isCaptain) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            Team senderTeam = teamRepository.findById(teamId).orElse(null);
            List<Negotiation> sentNegotiations = negotiationRepository.findBySenderTeamAndRoundNumber(senderTeam, game.getRoundNum());
            List<String> teamNamesWithSentNegotiations = sentNegotiations.stream()
                    .map(negotiation -> negotiation.getRecipientTeam().getTeamName())
                    .collect(Collectors.toList());

            List<String> teamNames = game.getTeams().stream()
                    .filter(team -> !team.getId().equals(teamId) && !teamNamesWithSentNegotiations.contains(team.getTeamName()))
                    .map(Team::getTeamName)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(teamNames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

/*
    @GetMapping("/game/{gameId}/team/{teamId}/neg-req")
    public ResponseEntity<NegsForTeamResponse> getNegotiationRequests(@PathVariable Long gameId, @PathVariable Long teamId, Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check if the authenticated user is a player in the specified team with the role ROLE_CAPTAIN
            String authenticatedUsername = principal.getName();
            boolean isCaptain = game.getTeams().stream()
                    .filter(team -> team.getId().equals(teamId))
                    .flatMap(team -> team.getPlayers().stream())
                    .filter(player -> player.getUser().getUsername().equals(authenticatedUsername))
                    .map(Player::getPlayerRole)
                    .map(PlayerRole::getName)
                    .anyMatch(roleName -> roleName == EPlayerRole.ROLE_CAPTAIN);

            if (!isCaptain) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Team team = game.getTeams().stream()
                    .filter(t -> t.getId().equals(teamId))
                    .findFirst()
                    .orElse(null);

            if (team == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            NegotiationStatus negotiationStatus = negotiationStatusRepository.findByName(ENegotiationStatus.SENT).orElse(null);

            List<Negotiation> negotiations = negotiationRepository.findByRecipientTeamAndNegStatus(team, negotiationStatus);
            //List<Negotiation> negotiations = negotiationRepository.findByRecipientTeamAndNegStatus(team, ENegotiationStatus.SENT);

            List<NegForTeamResponse> negForTeamResponses = negotiations.stream()
                    .map(negotiation -> new NegForTeamResponse(negotiation.getSenderTeam().getId(),
                            negotiation.getSenderTeam().getTeamName(),
                            negotiation.getNegStatus().getName().toString()))
                    .collect(Collectors.toList());

            List<TeamInfoResponse> teamResponses = game.getTeams().stream()
                    .filter(t -> !t.getId().equals(teamId))
                    .map(t -> new TeamInfoResponse(t.getId(), t.getTeamName()))
                    .collect(Collectors.toList());

            NegsForTeamResponse negsForTeamResponse = new NegsForTeamResponse(negForTeamResponses, teamResponses);

            return new ResponseEntity<>(negsForTeamResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
*/

    @PutMapping("/game/{gameId}/team/{teamId}")
    public ResponseEntity<String> sendOrder(@PathVariable Long gameId, @PathVariable Long teamId, @RequestBody OrderRequest orderRequest) {
        // Получить экземпляр игры по gameId
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        // Получить экземпляр команды по teamId
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        if (team.isOrder()) {
            throw new IllegalStateException("Нельзя отдать приказ дважды за раунд");
        }
        // Проверка, является ли юнит разрушенным на развитие
        List<Long> unitsDevIds = orderRequest.getUnitsDevIds();
        if (unitsDevIds != null) {
            for (Long devId : unitsDevIds) {
                Unit unit = unitRepository.getById(devId);
                if (unit.isDestroyed()) {
                    throw new IllegalStateException("Невозможно развить разрушенный юнит.");
                }
            }
        }
        //юнит разрушен щиты
        List<Long> unitsShieldIds = orderRequest.getUnitsShieldIds();
        if (unitsShieldIds != null) {
            for (Long devId : unitsShieldIds) {
                Unit unit = unitRepository.getById(devId);
                if (unit.isDestroyed()) {
                    throw new IllegalStateException("Невозможно поставить щит над разрушенным юнитом.");
                }
                if (unit.isShieldPresence()) {
                    throw new IllegalStateException("Невозможно поставить щит дважды.");
                }
            }
        }
        //развитие технологии
        if (orderRequest.isNucTechDev()) {
            if (team.isNuclearTech()) {
                throw new IllegalStateException("Невозможно развить технологию дважды.");
            }
        }
        //создание бомб
        if (orderRequest.getBombDev() > 0) {
            if (!team.isNuclearTech()) {
                throw new IllegalStateException("Невозможно создать бомбы без технологии.");
            }
        }
        //сброс бомб
        List<Long> dropBombsIds = orderRequest.getDropBombsIds();
        if (dropBombsIds != null) {
            if (team.getNumberOfBombs() < dropBombsIds.size()) {
                throw new IllegalStateException("Невозможно cбросить больше бомб, чем есть в запасе.");
            }
        }
        //обработка стоимости
        int totalCost = 0;
        totalCost += unitsDevIds.size() * game.getParam().getDevelopmentCost();
        totalCost += unitsShieldIds.size() * game.getParam().getShieldCost();
        if (orderRequest.isEcoProgram()) {
            totalCost += game.getParam().getEcoProgramCost();
        }
        if (orderRequest.isNucTechDev()) {
            totalCost += game.getParam().getNuclearTechCost();
        }
        totalCost += orderRequest.getBombDev() * game.getParam().getBombCost();

        if (totalCost > team.getBudget()) {
            throw new IllegalStateException("Расходы превышают допустимый бюджет");
        }
        // Обработка приказа
        // Используйте orderRequest для получения необходимых данных и выполнения соответствующих действий

        // Пример обработки приказа
        if (unitsDevIds != null) {
            for (Long devId : unitsDevIds) {
                Unit unit = unitRepository.getById(devId);
                unit.increaseLevelOfDevelopment(game.getParam().getDevelopmentChange());
                unitRepository.save(unit);
            }
        }

        if (unitsShieldIds != null) {
            for (Long devId : unitsShieldIds) {
                Unit unit = unitRepository.getById(devId);
                unit.setShieldOrder(true);
                unitRepository.save(unit);
            }
        }

        double ecoLevelNextRound = game.getEcoLevelNextRound();
        boolean ecoProgram = orderRequest.isEcoProgram();
        if (ecoProgram) {
            ecoLevelNextRound += game.getParam().getEcoProgramImpactOnEcology();
        }

        List<Long> sanctionsIds = orderRequest.getSanctionsIds();
        if (sanctionsIds != null) {
            List<Sanction> sanctionsToAdd = new ArrayList<>();
            for (Long teamTargetId : sanctionsIds) {
                Team targetTeam = teamRepository.getById(teamTargetId);
                Sanction sanction = new Sanction(game.getRoundNum(), team, targetTeam);
                sanctionsToAdd.add(sanction);
            }
            sanctionRepository.saveAll(sanctionsToAdd);
        }

        boolean nucTechDev = orderRequest.isNucTechDev();
        if (nucTechDev) {
            team.beginNuclearTechDev();
            ecoLevelNextRound -= game.getParam().getNuclearTechImpactOnEcology();
        }

        int bombDev = orderRequest.getBombDev();
        if (bombDev > 0) {
            team.addBombsInDev(bombDev);
            ecoLevelNextRound -= game.getParam().getBombConstructionImpactOnEcology();
        }

        if (dropBombsIds != null) {
            for (Long dropBombsId : dropBombsIds) {
                Unit unit = unitRepository.getById(dropBombsId);
                unit.bombardUnit();
                unitRepository.save(unit);
                //ecoLevelNextRound -= game.getParam().getBombDropImpactOnEcology();
            }
        }

        team.setBudget(team.getBudget() - totalCost);
        team.setOrder(true);
        teamRepository.save(team);
        game.setEcoLevelNextRound(ecoLevelNextRound);
        gameRepository.save(game);

        // Вернуть ответ с подтверждением выполнения приказа
        return ResponseEntity.ok("Order received and processed successfully.");
    }

    @PutMapping("/game/{gameId}/team/{teamId}/trans")
    public ResponseEntity<String> transferFunds(
            @PathVariable Long gameId,
            @PathVariable Long teamId,
            @RequestBody TransactionRequest transactionRequest,
            Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if the authenticated user is a player in the specified team with the role ROLE_CAPTAIN
            String authenticatedUsername = principal.getName();
            boolean isCaptain = game.getTeams().stream()
                    .filter(team -> team.getId().equals(teamId))
                    .flatMap(team -> team.getPlayers().stream())
                    .filter(player -> player.getUser().getUsername().equals(authenticatedUsername))
                    .map(Player::getPlayerRole)
                    .map(PlayerRole::getName)
                    .anyMatch(roleName -> roleName == EPlayerRole.ROLE_CAPTAIN);

            if (!isCaptain) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only team captains are allowed to transfer funds.");
            }

            Team senderTeam = teamRepository.findById(teamId).orElse(null);
            if (senderTeam == null) {
                return ResponseEntity.notFound().build();
            }

            Team receiverTeam = teamRepository.findById(transactionRequest.getReceiverTeamId()).orElse(null);
            if (receiverTeam == null) {
                return ResponseEntity.notFound().build();
            }

            int amount = transactionRequest.getAmount();
            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid transfer amount. Amount must be a positive integer.");
            }

            int senderBudget = senderTeam.getBudget();
            if (amount > senderBudget) {
                return ResponseEntity.badRequest().body("Insufficient funds. The sender team does not have enough budget.");
            }

            senderTeam.setBudget(senderBudget - amount);
            receiverTeam.setBudget(receiverTeam.getBudget() + amount);
            teamRepository.save(senderTeam);
            teamRepository.save(receiverTeam);

            return ResponseEntity.ok("Funds transferred successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/game/{gameId}/new-round")
    public ResponseEntity<String> startNewRound(
            @PathVariable Long gameId,
            @RequestBody NewRoundRequest newRoundRequest) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            List<Team> teams = game.getTeams();
            boolean allTeamsOrdered = teams.stream().allMatch(Team::isOrder);

            String reqStatus = newRoundRequest.getReqStatus();
            if (reqStatus.equals("normal") && !allTeamsOrdered) {
                return ResponseEntity.badRequest().body("Not all teams have submitted orders.");
            }

            RoundStatus roundStatus = game.getRoundStatus();
            int roundNum = game.getRoundNum();
            int numRounds = game.getParam().getNumRounds();

            if (roundStatus.getName() == ERoundStatus.GENERAL_ROUND) {
                roundStatus.setName(ERoundStatus.INNER_ROUND);
            } else if (roundStatus.getName() == ERoundStatus.INNER_ROUND) {
                if (roundNum == numRounds) {
                    roundStatus.setName(ERoundStatus.FINAL_ROUND);
                } else {
                    roundStatus.setName(ERoundStatus.GENERAL_ROUND);

                    for (Team team : teams) {
                        List<Unit> units = team.getUnits();
                        for (Unit unit : units) {
                            boolean wasDestroyed = unit.isDestroyed();
                            // Обновление юнита (ваша реализация)
                            unit.unitUpdate();
                            if (!wasDestroyed && unit.isDestroyed()) {
                                // Юнит стал разрушен, вычитаем уровень влияния сброса бомб
                                game.setEcoLevelNextRound(game.getEcoLevelNextRound() - game.getParam().getBombDropImpactOnEcology());
                            }
                            unitRepository.save(unit);
                        }
                        String incomeChangesStr = game.getParam().getIncomeChanges();
                        Expression incomeChanges = new ExpressionBuilder(incomeChangesStr)
                                .variables("I", "S", "D", "Y", "T")
                                .build();
                        Map<String, Double> variables = new HashMap<>();
                        variables.put("I", (double) game.getParam().getNetCityIncome()); // чистый доход
                        int roundNumber = roundNum - 1; // Номер прошлого раунда
                        List<Sanction> sanctions = sanctionRepository.findByTargetTeamAndRoundNumber(team, roundNumber);
                        int sanctionsCount = sanctions.size();
                        variables.put("S", (double) sanctionsCount); // количество санкций
                        variables.put("D", team.getDevLevelSum()); // уровень развития
                        variables.put("Y", game.getEcoLevelCurrentRound()); // экология
                        variables.put("T", (double) game.getTeams().size()); // количество команд

                        incomeChanges.setVariables(variables);
                        int profit = (int) Math.floor(incomeChanges.evaluate());
                        team.addBudget(profit);
                        teamRepository.save(team);
                    }

                    if (game.getEcoLevelNextRound() > 1) {
                        game.setEcoLevelNextRound(1);
                    } else if (game.getEcoLevelNextRound() < 0) {
                        game.setEcoLevelNextRound(0);
                    }
                    game.setEcoLevelCurrentRound(game.getEcoLevelNextRound());
                    game.setRoundNum(roundNum + 1);
                }
            }

            // Update the game entity and round status in the database
            game.setRoundStatus(roundStatus);
            gameRepository.save(game);

            return ResponseEntity.ok("New round started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/game/{gameId}/team/{teamId}/neg-res")
    public ResponseEntity<String> createNegotiation(
            @PathVariable Long gameId,
            @PathVariable Long teamId,
            @RequestBody NegRequest negRequest) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            Team senderTeam = teamRepository.findById(teamId).orElse(null);
            Team recipientTeam = teamRepository.findById(Long.parseLong(negRequest.getRecipientTeamId())).orElse(null);
            if (senderTeam == null || recipientTeam == null) {
                return ResponseEntity.notFound().build();
            }

            RoundStatus roundStatus = game.getRoundStatus();
            if (roundStatus.getName() != ERoundStatus.INNER_ROUND) {
                return ResponseEntity.badRequest().body("Negotiations can only be initiated during the inner round.");
            }

            int roundNum = game.getRoundNum();
            NegotiationStatus negotiationStatus = negotiationStatusRepository.findByName(ENegotiationStatus.SENT).orElse(null);

            // Проверяем наличие существующих переговоров
            Negotiation existingNegotiation = negotiationRepository.findNegotiationByRoundNumberAndSenderTeamAndRecipientTeam(roundNum, senderTeam, recipientTeam).orElse(null);
            if (existingNegotiation != null) {
                return ResponseEntity.badRequest().body("Negotiation request already exists for the specified teams in this round.");
            }

            Negotiation negotiation = new Negotiation(roundNum, senderTeam, recipientTeam, negotiationStatus);
            negotiationRepository.save(negotiation);

            return ResponseEntity.ok("Negotiation created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/game/{gameId}/team/{teamId}/neg-req")
    public ResponseEntity<NegsForTeamResponse> getNegotiationRequestsForTeam(
            @PathVariable Long gameId,
            @PathVariable Long teamId) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            Team playerTeam = teamRepository.findById(teamId).orElse(null);
            if (playerTeam == null) {
                return ResponseEntity.notFound().build();
            }

            RoundStatus roundStatus = game.getRoundStatus();
            if (roundStatus.getName() != ERoundStatus.INNER_ROUND) {
                return ResponseEntity.badRequest().build();
            }

            int roundNumber = game.getRoundNum();
            List<Negotiation> negotiations = negotiationRepository.findByRoundNumberAndRecipientTeam(roundNumber, playerTeam);
            List<NegForTeamResponse> negForTeamResponses = new ArrayList<>();

            for (Negotiation negotiation : negotiations) {
                Team recipientTeam = negotiation.getSenderTeam();
                NegotiationStatus negotiationStatus = negotiation.getNegStatus();
                NegForTeamResponse negForTeamResponse = new NegForTeamResponse(
                        recipientTeam.getId(),
                        recipientTeam.getTeamName(),
                        negotiationStatus.getName().toString()
                );
                negForTeamResponses.add(negForTeamResponse);
            }

            List<Team> allTeamsExceptPlayer = teamRepository.findAllByIdNot(teamId);
            List<TeamInfoResponse> teamInfoResponses = new ArrayList<>();

            for (Team team : allTeamsExceptPlayer) {
                TeamInfoResponse teamInfoResponse = new TeamInfoResponse(
                        team.getId(),
                        team.getTeamName()
                );
                teamInfoResponses.add(teamInfoResponse);
            }

            NegsForTeamResponse negsForTeamResponse = new NegsForTeamResponse(negForTeamResponses, teamInfoResponses);
            return ResponseEntity.ok(negsForTeamResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/game/{gameId}/team/{teamId}/neg-req")
    public ResponseEntity<String> respondToNegotiationRequest(
            @PathVariable Long gameId,
            @PathVariable Long teamId,
            @RequestBody NegAnswRequest negAnswRequest) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            Team senderTeam = teamRepository.findById(negAnswRequest.getRecipientTeamId()).orElse(null);
            Team recipientTeam = teamRepository.findById(teamId).orElse(null);
            if (senderTeam == null || recipientTeam == null) {
                return ResponseEntity.notFound().build();
            }

            Negotiation negotiation = negotiationRepository.findNegotiationByRoundNumberAndSenderTeamAndRecipientTeam(
                    game.getRoundNum(),
                    senderTeam,
                    recipientTeam
            ).orElse(null);

            if (negotiation == null) {
                return ResponseEntity.notFound().build();
            }

            NegotiationStatus currentStatus = negotiation.getNegStatus();
            NegotiationStatus newStatus;
            if (Objects.equals(negAnswRequest.getStatus(), "ACCEPTED")) {
                newStatus = negotiationStatusRepository.findByName(ENegotiationStatus.ACCEPTED).orElse(null);
            } else if (Objects.equals(negAnswRequest.getStatus(), "CANCELLED")) {
                newStatus = negotiationStatusRepository.findByName(ENegotiationStatus.ACCEPTED).orElse(null);
            } else {
                return ResponseEntity.badRequest().body("Invalid negotiation status.");
            }

            //NegotiationStatus newStatus = negotiationStatusRepository.findByName(negAnswRequest.getStatus()).orElse(null);

            if (newStatus == null) {
                return ResponseEntity.badRequest().body("Invalid negotiation status.");
            }

            if (currentStatus.getName() == ENegotiationStatus.CANCELLED ||
                    currentStatus.getName() == ENegotiationStatus.ACCEPTED ||
                    currentStatus.getName() == ENegotiationStatus.ENDED) {
                return ResponseEntity.badRequest().body("Cannot change the status of a cancelled, accepted, or ended negotiation.");
            }

            negotiation.setNegStatus(newStatus);
            negotiationRepository.save(negotiation);

            return ResponseEntity.ok("Negotiation status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/game/{gameId}/negs")
    public ResponseEntity<?> getNegotiationRequestsForHost(@PathVariable Long gameId, Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if the authenticated user is the host of the game
            String authenticatedUsername = principal.getName();
            if (!game.getHost().getUsername().equals(authenticatedUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            RoundStatus roundStatus = game.getRoundStatus();
            if (roundStatus.getName() != ERoundStatus.INNER_ROUND) {
                return ResponseEntity.badRequest().body("Negotiations can only be viewed during the inner round.");
            }

            List<Team> teams = game.getTeams();
            List<NegForHostResponse> negForHostResponses = new ArrayList<>();

            for (Team team : teams) {
                List<Negotiation> negotiations = negotiationRepository.findByRecipientTeam(team);

                List<NegForHostResponse> teamNegotiations = negotiations.stream()
                        .map(negotiation -> new NegForHostResponse(
                                negotiation.getSenderTeam().getId(),
                                negotiation.getSenderTeam().getTeamName(),
                                negotiation.getRecipientTeam().getId(),
                                negotiation.getRecipientTeam().getTeamName(),
                                negotiation.getNegStatus().getName().toString()))
                        .collect(Collectors.toList());

                negForHostResponses.addAll(teamNegotiations);
            }

            NegsForHostResponse negsForHostResponse = new NegsForHostResponse(negForHostResponses);

            return ResponseEntity.ok(negsForHostResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/game/{gameId}/negs")
    public ResponseEntity<?> respondToNegotiationRequestsForHost(
            @PathVariable Long gameId,
            @RequestBody NegHostAnswRequest negHostAnswRequest,
            Principal principal) {
        try {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if the authenticated user is the host of the game
            String authenticatedUsername = principal.getName();
            if (!game.getHost().getUsername().equals(authenticatedUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            RoundStatus roundStatus = game.getRoundStatus();
            if (roundStatus.getName() != ERoundStatus.INNER_ROUND) {
                return ResponseEntity.badRequest().body("Negotiations can only be responded to during the inner round.");
            }

            Long senderTeamId = negHostAnswRequest.getSenderTeamId();
            Long recipientTeamId = negHostAnswRequest.getRecipientTeamId();
            String status = negHostAnswRequest.getStatus();

            // Retrieve the negotiation based on the provided teams and round number
            Negotiation negotiation = negotiationRepository.findNegotiationByRoundNumberAndSenderTeamAndRecipientTeam(
                    game.getRoundNum(),
                    teamRepository.findById(senderTeamId).orElse(null),
                    teamRepository.findById(recipientTeamId).orElse(null)
            ).orElse(null);

            if (negotiation == null) {
                return ResponseEntity.notFound().build();
            }

            NegotiationStatus currentStatus = negotiation.getNegStatus();

            // Check if the current status allows the host to change the status
            if (currentStatus.getName() == ENegotiationStatus.CANCELLED ||
                    currentStatus.getName() == ENegotiationStatus.ENDED) {
                return ResponseEntity.badRequest().body("Cannot change the status of a cancelled, or ended negotiation.");
            }

            NegotiationStatus newStatus;
            if (status.equals(ENegotiationStatus.CANCELLED.name())) {
                newStatus = negotiationStatusRepository.findByName(ENegotiationStatus.CANCELLED).orElse(null);
            } else if (status.equals(ENegotiationStatus.ENDED.name())) {
                newStatus = negotiationStatusRepository.findByName(ENegotiationStatus.ENDED).orElse(null);
            } else {
                return ResponseEntity.badRequest().body("Invalid negotiation status.");
            }

            if (newStatus == null) {
                return ResponseEntity.badRequest().body("Invalid negotiation status.");
            }

            negotiation.setNegStatus(newStatus);
            negotiationRepository.save(negotiation);

            return ResponseEntity.ok("Negotiation status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @PutMapping("/exp")
    public ResponseEntity<?> experiment() {
        Team team = teamRepository.findById(1L).orElse(null);
        for (Unit unit : team.getUnits()) {
            unit.experiment();
            unitRepository.save(unit);
        }
        teamRepository.save(team);
        return new ResponseEntity<>(HttpStatus.OK);
    }

/*
    @GetMapping("/create-game")                 //настройки игры (название параметров и количество команд и юнитов и их данные) ГОТОВО
    @PostMapping("/create-game")                //создать игру с параматрами, но без команд ГОТОВО

    @GetMapping("/create-game/params")          //показать доступные параметры ГОТОВО
    @PostMapping("/create-game/params")         //создать новые параметры ГОТОВО

    @GetMapping("/games")                       //показать доступные игры ГОТОВО

    @GetMapping("/join-game/{id}")              //показать игру ГОТОВО
    @PutMapping("/join-game/{id}")              //присоединиться к игре и команде? ГОТОВО
    @DeleteMapping("/join-game/{id}")           //выйти из игры ГОТОВО

    @PutMapping("/start-game/{id}")             //начать игру ГОТОВО

    @GetMapping("/game{id}")                    //текущая статистика игры ГОТОВО

    @PutMapping("/game{id}/new-round")          //перейти на следующий раунд ГОТОВО(возможны исправления)

    @GetMapping("/game{id}/team/{id}")          //статы по команде ГОТОВО
    @PutMapping("/game{id}/team/{id}")          //отдать приказ ГОТОВО (возможны исправления)

    @GetMapping("/game{id}/team{id}/trans")     //страны для кидания денег ГОТОВО
    @PutMapping("/game{id}/team{id}/trans")     //кинуть денег ГОТОВО

    @GetMapping("/game{id}/team{id}/neg-req")   //страны, ждущие ответа на запрос ГОТОВО (нужно проверить)
    @PutMapping("/game{id}/team{id}/neg-req")   //ответ на запрос ГОТОВО (очень сильно нужно проверить)

    @GetMapping("/game{id}/team{id}/neg-res")   //страны для запросов ГОТОВО
    @PostMapping("/game{id}/team{id}/neg-res")  //кинуть запрос ГОТОВО (нужно проверять)

    @GetMapping("/game{id}/negs")               //все запросы для хоста  ГОТОВО нужно тестить
    @PutMapping("/game{id}/negs")               //ответить на запросы    ГОТОВО нужно тестить
    */
}

