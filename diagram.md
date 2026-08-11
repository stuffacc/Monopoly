# Class Diagram

```mermaid
classDiagram
    direction LR
    class GameState {
        +boolean isFinished
        +List~Player~ players
        +List~Cell~ cells
        +int turnCount
        +int playerTurn
        +GameTurnPhase gameTurnPhase
        +Pair~Int,Int~ lastDices
    }
    class GameTurnPhase~enum~ {
        START_TURN
        BUY_PROPERTY
        END_TURN
    }

    class Player {
        +String id
        +String name
        +Color color
        +int position
        +PlayerState playerState
        +int balance
        +int turnsInJail
        +int doubleCount
    }
    class PlayerState~enum~ {
        IN_JAIL
        IN_GAME
        NOT_IN_GAME
    }

    class Cell~sealed~ {
        +String name
    }
    class GoCell
    class StreetCell
    class CommunityChestCell
    class ChanceCell
    class TaxCell {
        +String taxName
        +int taxValue
    }
    class RailroadCell
    class UtilityCell
    class JailCell
    class FreeParkingCell
    class GoToJailCell

    class Property~sealed~ {
        +String name
        +int cost
        +Integer ownerIndex
    }
    class PropertyStreet {
        +Color streetColor
        +int improvementLevel
    }

    class Color~enum~ {
        BROWN
        YELLOW
        BLUE
        DARK_BLUE
        VIOLET
        ORANGE
        RED
        GREEN
        BLACK
    }





    class GameAction~sealed~ {
    }
    class ThrowDiceAction {
        +int dice1
        +int dice2
    }
    class BuyPropertyAction {
        +int cellId
    }
    class BuyUpgradeAction {
        +int cellId
    }
    class SellUpgradeAction {
        +int cellId
    }
    class EndTurnAction



    class GameChange~sealed~ {
    }
    class PlayerMoved {
        +int playerIndex
        +int from
        +int value
        +int to
    }
    class ChangePlayerState {
        +int playerIndex
        +PlayerState newState
    }
    class SetPropertyOwner {
        +Integer playerIndex
        +int propertyIndex
    }
    class ChangeGamePhase {
        +GameTurnPhase previousPhase
        +GameTurnPhase nextPhase
    }
    class SetPlayerDoubleCount {
        +int playerIndex
        +int doubleCount
    }
    class NextTurnGame {
        +int playerIndexBefore
        +int playerIndexAfter
    }
    class ChangeGameIsFinished {
        +boolean isFinished
    }
    class SetTurnsInJail {
        +int playerIndex
        +int turnsInJail
    }
    class SetUpgradeLevel {
        +int cellIndex
        +int upgradeLevel
    }
    class MakeTransaction {
        +Integer fromPlayerIndex
        +Integer toPlayerIndex
        +int amount
    }
    class SetRecentDices {
        +Pair~Int,Int~ dices
    }
    GameState --> GameTurnPhase

    GameAction <|-- ThrowDiceAction
    GameAction <|-- BuyPropertyAction
    GameAction <|-- BuyUpgradeAction
    GameAction <|-- SellUpgradeAction
    GameAction <|-- EndTurnAction
    GameChange <|-- PlayerMoved
    GameChange <|-- ChangePlayerState
    GameChange <|-- SetPropertyOwner
    GameChange <|-- ChangeGamePhase
    GameChange <|-- SetPlayerDoubleCount
    GameChange <|-- NextTurnGame
    GameChange <|-- ChangeGameIsFinished
    GameChange <|-- SetTurnsInJail
    GameChange <|-- SetUpgradeLevel
    GameChange <|-- MakeTransaction
    GameChange <|-- SetRecentDices
    
    Cell <|-- GoCell
    Cell <|-- StreetCell
    Cell <|-- CommunityChestCell
    Cell <|-- ChanceCell
    Cell <|-- TaxCell
    Cell <|-- RailroadCell
    Cell <|-- UtilityCell
    Cell <|-- JailCell
    Cell <|-- FreeParkingCell
    Cell <|-- GoToJailCell
    Property <|-- PropertyStreet
    StreetCell --> PropertyStreet : propertyStreet
    
    
    Player --> PlayerState
    PropertyStreet --> Property
```
