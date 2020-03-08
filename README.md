# IJA Projekt
![IJA Projekt](https://github.com/FJerabek/ija-proj1/workflows/IJA%20Projekt/badge.svg?branch=master)

Zadání: Navrhněte a implementujte aplikaci pro zobrazení linek hromadné dopravy a sledování jejich pohybu. Poznámka: Zadání definuje podstatné vlastnosti aplikace, které musí být splněny. Předpokládá se, že detaily řešení si doplní řešitelské týmy. Nejasnosti v zadání řešte, prosím, primárně na k tomu příslušném Fóru.

## Specifikace požadavků
### Základní požadavky
- aplikace zobrazí mapový podklad, na který poté přenáší informace o linkách
    - základní mapový podklad je tvořen pouze liniemi (čáry/lomené čáry mezi body křížení)
    - každá linie (čára) reprezentuje jednu ulici (může mít jméno)
    - tento základní koncept můžete jakkoliv rozšířit
    - mapový podklad se načte po spuštění ze souboru (formát je na vašem uvážení)
    - je možné mapový podklad přibližovat a oddalovat (zoom)
- systém hromadné dopravy
    - je členěn do linek (např. autobusová linka č. 41)
    - každá linka je definována seznamem zastávek (konečná - průběžné - konečná)
    - zastávka vždy leží na některé z ulic
    - každá linka obsahuje jednotlivé spoje (spoj je jedna kompletní cesta z jedné konečné zastávky do druhé konečné zastávky)
    - linka má jízdní řád, který obsahuje informace o jednotlivých spojích
    - linky a jejich jízdní řády se načítají ze souboru (formát je na vašem uvážení, lze využít některý z dostupných formátů, např. GTFS)
### Pohyb vozidel (spojů)
- systém obsahuje vlastní hodiny, které lze nastavit na výchozí hodnotu a různou rychlost
- po načtení mapy a linek začne systém zobrazovat jednotlivé spoje, které jsou právě na cestě (způsob zobrazení je na vaší invenci, postačí značka, kolečko, ...)
- symbol spoje je postupně posunuje podle aktuálního času a jízdního řádu (aktualizace zobrazení může být např. každých N sekund); pohyb spoje na trase je tedy simulován
- aktuální polohu na mapě postačí dopočítat podle délky trasy mezi zastávkami, jízdního řádu a vnitřních hodin aplikace; v tomto režimu tedy spoje nemají zpoždění
- po najetí/kliknutí na symbol spoje se zvýrazní trasa v mapě a zobrazí itinerář spoje (např. ve spodní části čára se zastávkami, časy odjezdů ze zastávek a aktuální pozice spoje)
### Interaktivní zásahy
- možnost definovat ztížené dopravní situace (stupně provozu)
    - stupeň provozu se týká celé ulice rovnoměrně
    - čím vyšší stupeň, tím pomalejší průjezd
    - vlivem vyšších stupňů provozu dochází ke zpoždění spojů
- možnost uzavření ulice a definování objízdné trasy
    - objízdná trasa se vytvoří manuálně (např. postupným naklikáním cesty)
    - pro celou objízdnou trasu se nastaví pevná hodnota zpoždění
    - objízdá trasa může vynechat některou ze zastávek
### Inspirace
- https://mapa.idsjmk.cz/
- https://tracker.geops.ch
- https://www.miamigov.com/files/sharedassets/public/trolley/brickell-trolley-map.pdf
