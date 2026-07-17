package org.example.project.data.models.cell

sealed class Cell(val name: String)

class GoCell: Cell(name = "Go")

class StreetCell(val propertyStreet: PropertyStreet): Cell(name = propertyStreet.name)

class CommunityChestCell: Cell(name = "Community Chest")

class ChanceCell: Cell(name = "Chance")

class TaxCell(val taxName: String, val taxValue: Int): Cell(name = taxName)

class RailroadCell: Cell(name = "Railroad")

class UtilityCell: Cell(name = "Electric")

class JailCell: Cell(name = "Jail")

class FreeParkingCell: Cell(name = "Free Parking")

class GoToJailCell: Cell(name = "GO TO JAIL!")








