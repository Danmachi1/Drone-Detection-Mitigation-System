@echo off
echo Creating nofly_zones.csv...
(
echo id,x,y,radius
echo NFZ-1,500,800,150
echo NFZ-2,-200,300,100
echo NFZ-3,1000,1200,200
) > nofly_zones.csv

echo Creating terrain_grid.csv...
(
echo x,y,z
echo 0,0,100
echo 1,0,105
echo 0,1,110
echo 1,1,115
echo 2,0,120
echo 2,1,125
echo 0,2,130
echo 1,2,135
echo 2,2,140
) > terrain_grid.csv

echo Creating elevation.csv...
(
echo x,y,elevation
echo 0,0,100
echo 1,0,105
echo 0,1,110
echo 1,1,115
echo 2,0,120
echo 2,1,125
echo 0,2,130
echo 1,2,135
echo 2,2,140
) > elevation.csv

echo All CSV files created successfully.
pause
