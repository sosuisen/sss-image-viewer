#!/bin/bash

# Source image
SOURCE="images/sample.png"

# Check if source exists
if [ ! -f "$SOURCE" ]; then
    echo "Error: $SOURCE not found!"
    exit 1
fi

# Create output directory
mkdir -p images_test

echo "Converting $SOURCE to all supported formats..."

# Common formats
convert "$SOURCE" images_test/sample.bmp
convert "$SOURCE" images_test/sample.gif
convert "$SOURCE" images_test/sample.jpg
convert "$SOURCE" images_test/sample.jpeg
convert "$SOURCE" images_test/sample.jfif
convert "$SOURCE" images_test/sample.png
convert "$SOURCE" images_test/sample.tiff
convert "$SOURCE" images_test/sample.tif

# Icon formats
convert "$SOURCE" -resize 256x256 images_test/sample.ico
convert "$SOURCE" -resize 128x128 images_test/sample.icns

# PCX format
convert "$SOURCE" images_test/sample.pcx
convert "$SOURCE" images_test/sample.dcx

# Netpbm formats
convert "$SOURCE" images_test/sample.pnm
convert "$SOURCE" -colorspace Gray images_test/sample.pgm
convert "$SOURCE" -monochrome images_test/sample.pbm
convert "$SOURCE" images_test/sample.ppm
convert "$SOURCE" images_test/sample.pam

# Photoshop
convert "$SOURCE" images_test/sample.psd

# HDR format
convert "$SOURCE" -colorspace RGB images_test/sample.hdr

# Wireless bitmap (monochrome, small)
convert "$SOURCE" -resize 48x48 -monochrome images_test/sample.wbmp

# X11 bitmap formats
convert "$SOURCE" -resize 64x64 -monochrome images_test/sample.xbm
convert "$SOURCE" -resize 64x64 images_test/sample.xpm

echo "All test images created in images_test/ directory!"
echo ""
echo "Created files:"
ls -la images_test/sample.*
echo ""
echo "Total files: $(ls images_test/sample.* | wc -l)"
echo ""

# Create EXIF orientation test images
echo "Creating EXIF orientation test images..."

# Use the JPG version as source for EXIF tests
EXIF_SOURCE="images_test/sample.jpg"

# Create copies with different EXIF orientation values using numeric format
cp "$EXIF_SOURCE" images_test/orientation_1_normal.jpg
exiftool -overwrite_original -Orientation#=1 images_test/orientation_1_normal.jpg

cp "$EXIF_SOURCE" images_test/orientation_2_flip_h.jpg
exiftool -overwrite_original -Orientation#=2 images_test/orientation_2_flip_h.jpg

cp "$EXIF_SOURCE" images_test/orientation_3_rot180.jpg
exiftool -overwrite_original -Orientation#=3 images_test/orientation_3_rot180.jpg

cp "$EXIF_SOURCE" images_test/orientation_4_flip_v.jpg
exiftool -overwrite_original -Orientation#=4 images_test/orientation_4_flip_v.jpg

cp "$EXIF_SOURCE" images_test/orientation_5_transpose.jpg
exiftool -overwrite_original -Orientation#=5 images_test/orientation_5_transpose.jpg

cp "$EXIF_SOURCE" images_test/orientation_6_rot90cw.jpg
exiftool -overwrite_original -Orientation#=6 images_test/orientation_6_rot90cw.jpg

cp "$EXIF_SOURCE" images_test/orientation_7_transverse.jpg
exiftool -overwrite_original -Orientation#=7 images_test/orientation_7_transverse.jpg

cp "$EXIF_SOURCE" images_test/orientation_8_rot90ccw.jpg
exiftool -overwrite_original -Orientation#=8 images_test/orientation_8_rot90ccw.jpg

echo ""
echo "EXIF Orientation test images created!"
echo "EXIF Orientation verification:"
for file in images_test/orientation_*.jpg; do
    echo "$(basename "$file"): $(exiftool -Orientation -s3 "$file")"
done
echo ""
echo "These images have EXIF orientation tags but are NOT physically rotated."
echo "EXIF-aware viewers should display them rotated according to the orientation tag."