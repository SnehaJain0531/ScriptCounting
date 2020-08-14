#!/usr/bin/env python
# coding: utf-8

# In[23]:


import os
import cv2
import numpy as np
import matplotlib.pyplot as plt


# In[24]:


def mode(results):
    vals = {}
    for x in results:
        if x not in vals:
            vals[x]=0
        vals[x]+=1
    
    #print(vals)
    vals = sorted(vals,key = lambda item : vals[item],reverse=True)
  
    return(vals[0])


# In[25]:


def binarization(image_name):
    image = cv2.imread(image_name)
    #image = cv2.resize(image,(1200,800))
    gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY)
    athresh=cv2.adaptiveThreshold(gray,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,151,15)
    path = 'I:\\Script Counting Testing\\Binarized Images\\'
    number = image_name.split('\\')[-1][:-4]
    bin_name = path + number + '.jpg'
    cv2.imwrite(bin_name,athresh)
    results=[]
    
    if image.shape[0] < 100:
        step_count=1
    else:
        step_count=image.shape[0]//100
    for x in range(0,image.shape[1],step_count): 
        dip_count=0
        y=[0]
        y[0]=athresh[0][x]
        for i in range(1,athresh.shape[0]):
            y.append(athresh[i][x])
        if(y[0]==255):
            in_white = 1
            white_start=0
        else:
            in_white = 0
        thicks = []
        for i in range(1,len(y)):
            if(in_white and y[i]==0):
                in_white = 0
                thicks.append(i-white_start)
                white_start = -1
            if(not in_white and y[i]==255):
                in_white = 1
                white_start = i
        invalid =[]
        lthresh = 0.3 * np.mean(thicks)
        uthresh = 2 * np.mean(thicks)
        
        if(len(thicks)>80):
            lthresh=0

        for i in thicks:
            if(i <lthresh or i >uthresh):
                invalid.append(i)
        dip_count = len(thicks) - len(invalid)
        
        if y[-1] == 0:
            dip_count-=1

        results.append(dip_count+1)
    return results,bin_name

    


# In[26]:


def smoothVals(y, count):
    WSZ = 3
    for i in range(WSZ-1, count):
        s = 0
        for j in range(i, i-WSZ, -1):
            s += y[j]
        y[i] = s/WSZ
    return y

def smooth(imgPath):
    # returns an array for which the mode can be taken
    img = cv2.imread(imgPath)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    height, width = gray.shape
    res =  []
    
    if width < 100:
        step_count=1
    else:
        step_count=width//100
    
    for j in range(0,width,step_count):
        y = [0 for i in range(height)]
        count = 0

        for i in range(height):
            y[count] = gray[i][j]
            count += 1
    
        for i in range(2):
            y = smoothVals(y, count)
        
        if (j==(50*step_count)):
            plt.plot(y)
            number = imgPath.split('\\')[-1][:-4]
            print(number)
            curve_name = 'Curve' + number + '.jpg'
            plt.savefig(curve_name)
            plt.close()
        
        dips = 0
        for i in range(1, count-1):
            if y[i-1] >= y[i] and y[i+1] > y[i]:
                dips += 1
        
        res.append(dips)
    #print(mode(res))
    return res


# In[27]:


def getScriptCount(image):
    results1,binarized_image = binarization(image)
    results2 = smooth(image); #smooth(image)
    results = results1 + results2
    return mode(results),binarized_image


# In[28]:


#getScriptCount('I:\Script Counting Testing\Test Images - Set 2 (Cropped)\C65.png')


# In[ ]:




