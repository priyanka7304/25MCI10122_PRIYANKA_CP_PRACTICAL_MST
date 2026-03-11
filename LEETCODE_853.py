class Solution(object):
    def carFleet(self, target, position, speed):
        """
        :type target: int
        :type position: List[int]
        :type speed: List[int]
        :rtype: int
        """
        cars_dict = {}
        for i in range(len(position)):
            cars_dict[position[i]] = speed[i]
        sorted_pos = sorted(cars_dict.keys(), reverse=True)
    
        f = 0
        max_time = 0  

        for pos in sorted_pos:
            spd = cars_dict[pos]
            time = (target - pos) / spd
            if time > max_time:
                f += 1
                max_time = time
    
        return f
