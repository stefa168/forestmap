import re
gml = open("bad.gml").read()
for i, pl in enumerate(re.findall(r"<gml:posList[^>]*>(.*?)</gml:posList>", gml, re.S)):
    v = pl.split()
    first = v[:2]
    last = v[-2:]
    if first != last:
        print(i, "tokens:", len(v), "| first:", first, "| last:", last)