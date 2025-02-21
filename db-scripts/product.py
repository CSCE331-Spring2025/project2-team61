from dataclasses import dataclass


@dataclass
class Product:
    category: str
    name: str
    price: int
    inventory: int

    def __str__(self):
        return f"('{self.category}', '{self.name}', {self.price}, {self.inventory})"


products = [
    Product(
        name="Classic Milk Tea",
        category="milk_tea",
        price=500,
        inventory=100,
    ),
    Product(
        name="Okinawa Milk Tea",
        category="milk_tea",
        price=550,
        inventory=80,
    ),
    Product(
        name="Mango Fruit Tea",
        category="fruit_tea",
        price=450,
        inventory=90,
    ),
    Product(
        name="Strawberry Fruit Tea",
        category="fruit_tea",
        price=470,
        inventory=85,
    ),
    Product(
        name="Earl Grey Tea",
        category="brewed_tea",
        price=300,
        inventory=120,
    ),
    Product(
        name="Jasmine Green Tea",
        category="brewed_tea",
        price=320,
        inventory=110,
    ),
    Product(
        name="Fresh Taro Milk",
        category="fresh_milk",
        price=600,
        inventory=75,
    ),
    Product(
        name="Matcha Fresh Milk",
        category="fresh_milk",
        price=620,
        inventory=70,
    ),
    Product(
        name="Mocha Ice Blended",
        category="ice_blended",
        price=650,
        inventory=60,
    ),
    Product(
        name="Caramel Ice Blended",
        category="ice_blended",
        price=670,
        inventory=55,
    ),
    Product(
        name="Lemon Tea Mojito",
        category="tea_mojito",
        price=550,
        inventory=50,
    ),
    Product(
        name="Passionfruit Tea Mojito",
        category="tea_mojito",
        price=580,
        inventory=45,
    ),
    Product(
        name="Oolong Crema",
        category="creama",
        price=520,
        inventory=65,
    ),
    Product(
        name="Black Tea Crema",
        category="creama",
        price=530,
        inventory=60,
    ),
    Product(
        name="Vanilla Ice Cream",
        category="ice_cream",
        price=400,
        inventory=150,
    ),
    Product(
        name="Chocolate Ice Cream",
        category="ice_cream",
        price=420,
        inventory=140,
    ),
    Product(
        name="Bottled Water",
        category="misc",
        price=200,
        inventory=200,
    ),
    Product(
        name="Canned Soda",
        category="misc",
        price=250,
        inventory=180,
    ),
    Product(
        name="Pearl Topping",
        category="topping",
        price=50,
        inventory=500,
    ),
    Product(
        name="Limited Edition Tea",
        category="special_item",
        price=700,
        inventory=30,
    ),
]
