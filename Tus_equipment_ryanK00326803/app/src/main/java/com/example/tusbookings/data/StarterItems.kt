package com.example.tusbookings.data

import com.example.tusbookings.model.Item
import com.example.tusbookings.model.ItemType

val starterTypes = listOf(
    ItemType(1, "Laptops"),
    ItemType(2, "Cameras"),
    ItemType(3, "Audio"),
    ItemType(4, "Tablets"),
    ItemType(5, "Accessories")
)

val starterItems = listOf(
    Item(
        id = 1, typeId = 1, name = "Dell Latitude 5440",
        description = "14 inch laptop with Office and IntelliJ installed.",
        location = "IT Desk, Moylish", condition = "Good",
        unitsAvailable = 4, unitsTotal = 4
    ),
    Item(
        id = 2, typeId = 2, name = "Canon EOS 250D",
        description = "DSLR camera with 18-55mm lens and spare battery.",
        location = "Media Lab B12", condition = "Very good",
        unitsAvailable = 2, unitsTotal = 2
    ),
    Item(
        id = 3, typeId = 3, name = "Rode Wireless GO II",
        description = "Two-person clip-on microphone set for interviews.",
        location = "Media Lab B12", condition = "Good",
        unitsAvailable = 3, unitsTotal = 3
    ),
    Item(
        id = 4, typeId = 4, name = "Samsung Galaxy Tab S9",
        description = "Android tablet for app testing, with stylus.",
        location = "Computing Lab C2", condition = "New",
        unitsAvailable = 5, unitsTotal = 5
    ),
    Item(
        id = 5, typeId = 5, name = "USB-C Multiport Hub",
        description = "HDMI, USB-A and Ethernet adapter for presentations.",
        location = "IT Desk, Moylish", condition = "Fair",
        unitsAvailable = 6, unitsTotal = 6
    ),
    Item(
        id = 6, typeId = 2, name = "GoPro Hero 12",
        description = "Action camera with chest mount and waterproof case.",
        location = "Media Lab B12", condition = "Good",
        unitsAvailable = 1, unitsTotal = 1
    )
)
