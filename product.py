from dataclasses import dataclass

@dataclass
class Product:
    category: str
    name: str
    price: int
    inventory: int

    def __str__(self):
        return f"('{self.category}', '{self.name}', {self.price}, {self.inventory})"
