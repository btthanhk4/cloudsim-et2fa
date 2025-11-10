#!/usr/bin/env python3
"""
Simple Montage DAX Generator - Không cần Pegasus WMS API
Generate DAX files cho Montage workflows mà không cần Pegasus WMS

Usage:
    python generate-montage-dax-simple.py --center "56.7 24.0" --degrees 1.0 --bands 1
    python generate-montage-dax-simple.py --center "56.7 24.0" --degrees 2.0 --bands 3
"""

import argparse
import xml.etree.ElementTree as ET
from xml.dom import minidom
import os
import math

def prettify(elem):
    """Return a pretty-printed XML string for the Element."""
    rough_string = ET.tostring(elem, 'utf-8')
    reparsed = minidom.parseString(rough_string)
    return reparsed.toprettyxml(indent="  ")

def generate_montage_dax(center, degrees, num_bands=1, output_file="montage-generated.dax"):
    """
    Generate a Montage workflow DAX file.
    
    Args:
        center: String like "56.7 24.0" (RA Dec)
        degrees: Float, size of mosaic in degrees
        num_bands: Number of bands (1-3)
        output_file: Output DAX file name
    """
    
    # Parse center coordinates
    ra, dec = map(float, center.split())
    
    # Calculate number of images based on degrees
    # Rough estimate: ~8 images per degree
    num_images = max(4, int(degrees * 8))
    
    # Create root element
    adag = ET.Element("adag")
    adag.set("xmlns", "http://pegasus.isi.edu/schema/DAX")
    adag.set("version", "3.6")
    adag.set("name", f"montage-{degrees}deg-{num_bands}bands")
    
    job_id_counter = 0
    jobs_by_level = {}
    
    # Generate jobs for each band
    for band in range(1, num_bands + 1):
        band_jobs = {}
        
        # Level 0: mProjectPP tasks (project images)
        level0_jobs = []
        for i in range(num_images):
            job_id = f"mProjectPP_{band}_{i}"
            job = ET.SubElement(adag, "job")
            job.set("id", job_id)
            job.set("name", "mProjectPP")
            runtime = 15 + (i % 5)  # Vary runtime 15-19 seconds
            job.set("runtime", str(runtime))
            
            uses = ET.SubElement(job, "uses")
            uses.set("name", f"projected_{band}_{i}.fits")
            uses.set("link", "output")
            uses.set("size", str(10485760))  # 10MB
            
            level0_jobs.append(job_id)
            job_id_counter += 1
        
        band_jobs[0] = level0_jobs
        
        # Level 1: mDiffFit tasks (difference fitting)
        # Each mDiffFit needs 2 mProjectPP outputs
        level1_jobs = []
        num_diffs = max(1, num_images - 1)
        for i in range(num_diffs):
            job_id = f"mDiffFit_{band}_{i}"
            job = ET.SubElement(adag, "job")
            job.set("id", job_id)
            job.set("name", "mDiffFit")
            runtime = 25 + (i % 4)  # Vary runtime 25-28 seconds
            job.set("runtime", str(runtime))
            
            uses = ET.SubElement(job, "uses")
            uses.set("name", f"diff_{band}_{i}.fits")
            uses.set("link", "output")
            uses.set("size", str(5242880))  # 5MB
            
            level1_jobs.append(job_id)
            job_id_counter += 1
        
        band_jobs[1] = level1_jobs
        
        # Level 2: mBgModel task (background model) - one per band
        job_id = f"mBgModel_{band}"
        job = ET.SubElement(adag, "job")
        job.set("id", job_id)
        job.set("name", "mBgModel")
        job.set("runtime", "30")
        
        uses = ET.SubElement(job, "uses")
        uses.set("name", f"bg_{band}.fits")
        uses.set("link", "output")
        uses.set("size", str(2097152))  # 2MB
        
        band_jobs[2] = [job_id]
        job_id_counter += 1
        
        # Level 3: mBgExec tasks (background execution)
        level3_jobs = []
        for i in range(num_images):
            job_id = f"mBgExec_{band}_{i}"
            job = ET.SubElement(adag, "job")
            job.set("id", job_id)
            job.set("name", "mBgExec")
            runtime = 20 + (i % 5)  # Vary runtime 20-24 seconds
            job.set("runtime", str(runtime))
            
            uses = ET.SubElement(job, "uses")
            uses.set("name", f"corrected_{band}_{i}.fits")
            uses.set("link", "output")
            uses.set("size", str(10485760))  # 10MB
            
            level3_jobs.append(job_id)
            job_id_counter += 1
        
        band_jobs[3] = level3_jobs
        
        # Level 4: mAdd tasks (add mosaics)
        # Group corrected images into smaller mosaics first
        num_add_tasks = max(1, int(math.ceil(num_images / 3)))
        level4_jobs = []
        for i in range(num_add_tasks):
            job_id = f"mAdd_{band}_{i}"
            job = ET.SubElement(adag, "job")
            job.set("id", job_id)
            job.set("name", "mAdd")
            runtime = 35 + (i % 5)  # Vary runtime 35-39 seconds
            job.set("runtime", str(runtime))
            
            uses = ET.SubElement(job, "uses")
            uses.set("name", f"mosaic_{band}_{i}.fits")
            uses.set("link", "output")
            uses.set("size", str(52428800))  # 50MB
            
            level4_jobs.append(job_id)
            job_id_counter += 1
        
        band_jobs[4] = level4_jobs
        
        # Level 5: Final mAdd task (combine all mosaics)
        if num_add_tasks > 1:
            job_id = f"mAdd_{band}_final"
            job = ET.SubElement(adag, "job")
            job.set("id", job_id)
            job.set("name", "mAdd")
            job.set("runtime", "40")
            
            uses = ET.SubElement(job, "uses")
            uses.set("name", f"final_mosaic_{band}.fits")
            uses.set("link", "output")
            uses.set("size", str(104857600))  # 100MB
            
            band_jobs[5] = [job_id]
        else:
            band_jobs[5] = []
        
        jobs_by_level[band] = band_jobs
    
    # Create dependencies
    for band, band_jobs in jobs_by_level.items():
        # Level 0 -> Level 1: mProjectPP -> mDiffFit
        level0 = band_jobs[0]
        level1 = band_jobs[1]
        for i, diff_job in enumerate(level1):
            child = ET.SubElement(adag, "child")
            child.set("ref", diff_job)
            
            # Each diff needs 2 project outputs
            parent1_idx = i
            parent2_idx = min(i + 1, len(level0) - 1)
            
            parent1 = ET.SubElement(child, "parent")
            parent1.set("ref", level0[parent1_idx])
            parent2 = ET.SubElement(child, "parent")
            parent2.set("ref", level0[parent2_idx])
        
        # Level 1 -> Level 2: mDiffFit -> mBgModel
        level1 = band_jobs[1]
        level2 = band_jobs[2]
        if level2:
            child = ET.SubElement(adag, "child")
            child.set("ref", level2[0])
            for diff_job in level1:
                parent = ET.SubElement(child, "parent")
                parent.set("ref", diff_job)
        
        # Level 0 + Level 2 -> Level 3: mProjectPP + mBgModel -> mBgExec
        level0 = band_jobs[0]
        level2 = band_jobs[2]
        level3 = band_jobs[3]
        for i, exec_job in enumerate(level3):
            child = ET.SubElement(adag, "child")
            child.set("ref", exec_job)
            
            # Each exec needs corresponding project and bg model
            if i < len(level0):
                parent1 = ET.SubElement(child, "parent")
                parent1.set("ref", level0[i])
            if level2:
                parent2 = ET.SubElement(child, "parent")
                parent2.set("ref", level2[0])
        
        # Level 3 -> Level 4: mBgExec -> mAdd
        level3 = band_jobs[3]
        level4 = band_jobs[4]
        images_per_add = max(1, int(math.ceil(len(level3) / len(level4))))
        for i, add_job in enumerate(level4):
            child = ET.SubElement(adag, "child")
            child.set("ref", add_job)
            
            start_idx = i * images_per_add
            end_idx = min(start_idx + images_per_add, len(level3))
            for j in range(start_idx, end_idx):
                parent = ET.SubElement(child, "parent")
                parent.set("ref", level3[j])
        
        # Level 4 -> Level 5: mAdd -> final mAdd
        level4 = band_jobs[4]
        level5 = band_jobs[5]
        if level5:
            child = ET.SubElement(adag, "child")
            child.set("ref", level5[0])
            for add_job in level4:
                parent = ET.SubElement(child, "parent")
                parent.set("ref", add_job)
    
    # Write to file
    xml_str = prettify(adag)
    # Remove XML declaration from prettify and add our own
    lines = xml_str.split('\n')
    if lines[0].startswith('<?xml'):
        lines = lines[1:]
    xml_str = '<?xml version="1.0" encoding="UTF-8"?>\n' + '\n'.join(lines).lstrip()
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(xml_str)
    
    print(f"✓ Generated DAX file: {output_file}")
    print(f"  Total jobs: {job_id_counter}")
    print(f"  Bands: {num_bands}")
    print(f"  Images per band: {num_images}")
    print(f"  Center: {center}")
    print(f"  Degrees: {degrees}")
    
    return output_file

def main():
    parser = argparse.ArgumentParser(
        description='Generate Montage workflow DAX file (without Pegasus WMS)'
    )
    parser.add_argument('--center', type=str, default="56.7 24.0",
                        help='Center coordinates (RA Dec), e.g., "56.7 24.0"')
    parser.add_argument('--degrees', type=float, default=1.0,
                        help='Size of mosaic in degrees (default: 1.0)')
    parser.add_argument('--bands', type=int, default=1,
                        help='Number of bands (1-3, default: 1)')
    parser.add_argument('--output', type=str, default="montage-generated.dax",
                        help='Output DAX file name (default: montage-generated.dax)')
    
    args = parser.parse_args()
    
    # Validate bands
    if args.bands < 1 or args.bands > 3:
        print("ERROR: --bands must be between 1 and 3")
        sys.exit(1)
    
    generate_montage_dax(
        center=args.center,
        degrees=args.degrees,
        num_bands=args.bands,
        output_file=args.output
    )

if __name__ == "__main__":
    import sys
    main()



