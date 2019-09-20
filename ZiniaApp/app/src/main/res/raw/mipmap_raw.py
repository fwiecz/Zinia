#import numpy as np
from sys import argv
from PIL import Image

paths = ["..\\mipmap-mdpi",
         "..\\mipmap-hdpi",
         "..\\mipmap-xhdpi",
         "..\\mipmap-xxhdpi",
         "..\\mipmap-xxxhdpi",]

filename = argv[1]

sizes = []

for size in argv[2:]:
    sizes.append( (int(size), int(size)) )

im = Image.open(filename)
im.load()

sizes.append(im.size)
print(sizes)

for d, nr in zip(paths, range(len(paths))):
    im = Image.open(filename)
    im.load()
    im.thumbnail( sizes[ min(len(sizes)-1, nr) ] )
    im.save(d + "\\" + filename, "PNG")