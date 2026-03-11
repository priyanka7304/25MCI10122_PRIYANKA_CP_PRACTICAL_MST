class Solution(object):
    def minimumSize(self, nums, maxOperations):
        # for i in range(maxOperations):
        #     m=max(nums)
        #     index=nums.index(m)
        #     nums[index]=m//2
        # return max(nums)
        low=1
        high=max(nums)
        ans=high
        while low<=high:
            mid=(low+high)//2
            op=0
            for i in nums:
                if i>mid:
                    op+=(i-1)//mid
            if op<= maxOperations:
                ans=mid
                high=mid-1
            else:
                low=mid+1
        return ans

        