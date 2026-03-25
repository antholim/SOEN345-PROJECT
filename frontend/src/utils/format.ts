export const formatJoinDate = (isoString: string): string =>
  new Date(isoString).toLocaleDateString('en-CA', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
